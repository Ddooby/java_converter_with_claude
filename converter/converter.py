import json
import logging
import re
from pathlib import Path

logger = logging.getLogger(__name__)

INPUT_DIR = Path("input")
OUTPUT_DIR = Path("output")
PATTERNS_FILE = Path("patterns/learned_patterns.json")


class RuleEngine:
    """patterns JSON의 치환 규칙을 코드로 적용."""

    def apply(self, code: str, patterns: dict) -> str:
        # 1. import 문자열 치환 (패키지 경로 변경 / 삭제)
        for rule in patterns.get("import_replacements", []):
            frm, to = rule.get("from", ""), rule.get("to", "")
            if frm:
                code = code.replace(frm, to)

        # 2. 특정 prefix 로 시작하는 import 라인 전체 제거
        prefix_removals = patterns.get("import_prefix_removals", [])
        if prefix_removals:
            lines = code.splitlines(keepends=True)
            code = "".join(
                line for line in lines
                if not any(line.strip().startswith(p) for p in prefix_removals)
            )

        # 3. 새 import 추가 (중복 제외, package 선언 바로 뒤에 삽입)
        additions = patterns.get("import_additions", [])
        for imp in additions:
            if imp not in code:
                code = re.sub(
                    r'(package\s+[\w.]+;\s*\n)',
                    r'\1' + imp + '\n',
                    code,
                    count=1
                )

        # 4. 어노테이션 치환
        for rule in patterns.get("annotation_replacements", []):
            frm, to = rule.get("from", ""), rule.get("to", "")
            if frm:
                code = re.sub(re.escape(frm) + r'\b', to, code)

        # 5. 일반 텍스트 치환
        for rule in patterns.get("text_replacements", []):
            frm, to = rule.get("from", ""), rule.get("to", "")
            if frm:
                code = code.replace(frm, to)

        return code


class DaoTransformer:
    """DAO 파일 변환 엔진 — API 없이 내장 규칙으로 처리."""

    # (정규식 패턴, 치환 문자열) 목록
    _LINE_RULES: list[tuple[str, str]] = [
        # DbWrap 선언 제거
        (r'\bDbWrap\s+\w+\s*=\s*new\s+DbWrap\(\)\s*;', ''),
        # setObject: conn 파라미터 제거
        (r'\b(\w+)\.setObject\(conn,\s*(\w+),\s*([^,)]+),\s*(\d+)\)',
         r'commonDao.setObject(\2, \3, \4)'),
        # getObject: conn 파라미터 제거
        (r'\b(\w+)\.getObject\(conn,\s*([^,)]+),\s*([^)]+)\)',
         r'commonDao.getObject(\2, \3)'),
        # Connection conn 파라미터 제거
        (r',\s*Connection\s+conn\b', ''),
        (r'\bConnection\s+conn\s*,\s*', ''),
        (r'\bConnection\s+conn\b', ''),
        # UserBean userBean 파라미터 제거
        (r',\s*UserBean\s+userBean\b', ''),
        (r'\bUserBean\s+userBean\s*,\s*', ''),
        # Formatter.nullTrim → StringUtil.nvl
        (r'\bFormatter\.nullTrim\(([^)]+)\)', r'StringUtil.nvl(\1, "")'),
        # ResultSet 컬럼 읽기 → Map 변환
        (r'\brs\d*\.getString\("(\w+)"\)', r'(String) map.get("\1")'),
        (r"\brs\d*\.getString\('(\w+)'\)", r'(String) map.get("\1")'),
        (r'\brs\d*\.getLong\("(\w+)"\)',
         r'StringUtil.toLong((String) map.get("\1"), 0L)'),
        (r'\brs\d*\.getDouble\("(\w+)"\)',
         r'StringUtil.toDouble((String) map.get("\1"), 0.0)'),
        (r'\brs\d*\.getInt\("(\w+)"\)',
         r'StringUtil.toInt((String) map.get("\1"), 0)'),
        # finally 블록 내 close() 제거
        (r'if\s*\(\s*rs\d*\s*!=\s*null\s*\)\s*rs\d*\.close\(\)\s*;', ''),
        (r'if\s*\(\s*ps\d*\s*!=\s*null\s*\)\s*ps\d*\.close\(\)\s*;', ''),
        (r'\brs\d*\.close\(\)\s*;', ''),
        (r'\bps\d*\.close\(\)\s*;', ''),
    ]

    def __init__(self, class_name: str):
        self.class_name = class_name
        self.namespace = re.sub(r'(?i)DAO$', '', class_name)

    def transform(self, code: str) -> tuple[str, str]:
        code = self._apply_line_rules(code)
        code = self._add_class_decorations(code)
        code, xml_entries = self._convert_execute_queries(code)
        return code, self._build_mapper_xml(xml_entries)

    # ------------------------------------------------------------------ #
    #  내부 메서드                                                          #
    # ------------------------------------------------------------------ #

    def _apply_line_rules(self, code: str) -> str:
        for pattern, replacement in self._LINE_RULES:
            code = re.sub(pattern, replacement, code)
        return code

    def _add_class_decorations(self, code: str) -> str:
        """@Slf4j / @RequiredArgsConstructor / @Repository 추가 및 필드 삽입."""
        anns = '@Slf4j\n@RequiredArgsConstructor\n@Repository\n'
        code = re.sub(r'(public\s+class\s+)', anns + r'\1', code, count=1)
        fields = (
            '\n    private final CommonDao commonDao;'
            '\n    private final UxbDAO uxbDAO;\n'
        )
        code = re.sub(r'(public\s+class\s+\w+[^{]*\{)', r'\1' + fields, code, count=1)
        return code

    def _convert_execute_queries(self, code: str) -> tuple[str, list[dict]]:
        """
        PreparedStatement + executeQuery 패턴을 uxbDAO.select() 로 변환하고
        SQL 은 Mapper XML 엔트리로 추출한다.
        """
        xml_entries: list[dict] = {}  # mapper_id → sql
        method_query_count: dict[str, int] = {}

        # ① 모든 conn.prepareStatement 위치를 찾아 순서대로 처리
        prep_re = re.compile(
            r'(\w+)\s*=\s*conn\.prepareStatement\((\w+)\.toString\(\)\)\s*;'
        )

        offset = 0
        result_code = code

        for prep_match in prep_re.finditer(code):
            sb_var = prep_match.group(2)

            # 해당 prepareStatement 가 속한 메서드 이름 파악
            method_name = self._enclosing_method(code, prep_match.start())
            if not method_name:
                continue

            # 같은 메서드 내 몇 번째 쿼리인지 결정 → Mapper ID
            idx = method_query_count.get(method_name, 0)
            method_query_count[method_name] = idx + 1
            mapper_id = method_name if idx == 0 else f"{method_name}{idx}"

            # SQL 및 파라미터 추출 (prepareStatement 이전 코드에서)
            before = code[:prep_match.start()]
            sql_parts, param_names = self._extract_sql_and_params(before, sb_var)
            sql_xml = ' '.join(sql_parts).strip()

            xml_entries[mapper_id] = {'id': mapper_id, 'sql': sql_xml, 'params': param_names}

            # paramMap 코드 생성
            param_map_lines = ['Map<String, Object> paramMap = new HashMap<>();']
            for p in param_names:
                param_map_lines.append(f'        paramMap.put("{p}", {p});')
            param_map_code = '\n        '.join(param_map_lines)

            select_call = (
                f'{param_map_code}\n'
                f'        List<Map<String, Object>> listMap = '
                f'uxbDAO.select("{self.namespace}.{mapper_id}", paramMap);'
            )

            # result_code 내에서 치환 (offset 기반 위치 재계산)
            adjusted_start = prep_match.start() + offset
            # sb 선언부터 prepareStatement 까지를 select_call 로 교체
            sb_decl_re = re.compile(
                rf'([ \t]*)(?:String\w*|StringBuilder|StringBuffer)\s+{re.escape(sb_var)}\s*=\s*new\s+\w+\(\)\s*;'
            )
            sb_match = sb_decl_re.search(result_code, max(0, adjusted_start - 3000))
            if sb_match and sb_match.start() < adjusted_start:
                replace_start = sb_match.start()
                # prepareStatement 라인 끝까지
                prep_line_end = result_code.index('\n', adjusted_start) + 1
                result_code = result_code[:replace_start] + select_call + '\n' + result_code[prep_line_end:]
                offset += len(select_call) + 1 - (prep_line_end - replace_start)

        # ② executeQuery() 잔여 호출 제거
        result_code = re.sub(
            r'[ \t]*\w+\s*=\s*\w+\.executeQuery\(\)\s*;\n?', '', result_code
        )

        # ③ while (rs.next()) → for (Map<String, Object> map : listMap)
        result_code = re.sub(
            r'while\s*\(\s*\w+\.next\(\)\s*\)',
            'for (Map<String, Object> map : listMap)',
            result_code
        )

        # ④ 빈 finally 블록 정리 (close() 가 제거된 후 빈 finally)
        result_code = re.sub(
            r'\s*finally\s*\{\s*(?:try\s*\{)?\s*\}?\s*(?:catch\s*\([^)]+\)\s*\{[^}]*\})?\s*\}',
            '',
            result_code
        )

        return result_code, list(xml_entries.values())

    def _enclosing_method(self, code: str, pos: int) -> str:
        """pos 이전에서 가장 가까운 메서드 이름을 반환."""
        pattern = re.compile(
            r'\b(?:public|private|protected)\s+\S+\s+(\w+)\s*\('
        )
        name = ''
        for m in pattern.finditer(code[:pos]):
            name = m.group(1)
        return name

    def _extract_sql_and_params(
        self, code: str, sb_var: str
    ) -> tuple[list[str], list[str]]:
        """sb.append() 호출에서 SQL 조각과 파라미터 이름을 추출."""
        sql_parts: list[str] = []
        param_names: list[str] = []

        append_re = re.compile(
            rf'\b{re.escape(sb_var)}\.append\((.+?)\)\s*;',
            re.DOTALL
        )

        for m in append_re.finditer(code):
            arg = m.group(1).strip()

            # 순수 문자열 리터럴
            if re.match(r'^"[^"]*"$', arg):
                sql_parts.append(arg[1:-1])
                continue

            # "prefix" + param[.method()] + "suffix"
            concat = re.match(
                r'"([^"]*?)"\s*\+\s*'
                r'([A-Za-z_][A-Za-z0-9_]*)(?:\.\w+\(\))?\s*\+\s*"([^"]*?)"',
                arg
            )
            if concat:
                sql_parts.append(concat.group(1))
                raw_param = concat.group(2)
                param_names.append(raw_param)
                sql_parts.append(f'#{{{raw_param}}}')
                sql_parts.append(concat.group(3))
                continue

            # "prefix" + param[.method()]  (suffix 없음)
            concat_no_suffix = re.match(
                r'"([^"]*?)"\s*\+\s*([A-Za-z_][A-Za-z0-9_]*)(?:\.\w+\(\))?$',
                arg
            )
            if concat_no_suffix:
                sql_parts.append(concat_no_suffix.group(1))
                raw_param = concat_no_suffix.group(2)
                param_names.append(raw_param)
                sql_parts.append(f'#{{{raw_param}}}')
                continue

            # detailSql 등 다른 변수 참조 (문자열이 아닌 경우)
            if not arg.startswith('"') and not arg.startswith("'"):
                sql_parts.append(f'/* {arg} */')

        return sql_parts, list(dict.fromkeys(param_names))  # 중복 제거, 순서 유지

    def _build_mapper_xml(self, entries: list[dict]) -> str:
        if not entries:
            return ''
        select_blocks = []
        for e in entries:
            select_blocks.append(
                f'    <select id="{e["id"]}" parameterType="map" resultType="map"'
                f' useCache="false" timeout="0">\n'
                f'        <![CDATA[\n'
                f'        /* {self.namespace}.{e["id"]} */\n'
                f'        ]]>\n'
                f'        {e["sql"]}\n'
                f'    </select>'
            )
        body = '\n\n'.join(select_blocks)
        return (
            '<?xml version="1.0" encoding="UTF-8"?>\n'
            '<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"\n'
            '    "http://mybatis.org/dtd/mybatis-3-mapper.dtd">\n'
            f'<mapper namespace="{self.namespace}">\n'
            f'{body}\n'
            '</mapper>'
        )


class EjbConverter:
    def __init__(self):
        self.rule_engine = RuleEngine()

    def _load_patterns(self) -> dict:
        if not PATTERNS_FILE.exists():
            raise FileNotFoundError(
                f"{PATTERNS_FILE} 파일이 없습니다. "
                "Claude Code 세션에서 샘플을 분석하여 패턴 파일을 먼저 생성하세요."
            )
        patterns = json.loads(PATTERNS_FILE.read_text(encoding="utf-8"))
        logger.info(f"패턴 로드: {PATTERNS_FILE}")
        return patterns

    def _is_dao_file(self, path: Path) -> bool:
        return path.stem.upper().endswith('DAO')

    def _mapper_name(self, stem: str) -> str:
        return re.sub(r'(?i)DAO$', '', stem) + 'Mapper.xml'

    def convert_file(self, source_path: Path, patterns: dict) -> dict | str:
        """단일 파일 변환. DAO 파일은 {'java': ..., 'xml': ...} 반환."""
        code = source_path.read_text(encoding='utf-8')
        code = self.rule_engine.apply(code, patterns)

        if self._is_dao_file(source_path):
            transformer = DaoTransformer(source_path.stem)
            java, xml = transformer.transform(code)
            return {'java': java, 'xml': xml}
        return code

    def convert_all(self) -> list[Path]:
        """input/ 폴더의 모든 .java 파일을 변환하여 output/ 에 저장."""
        patterns = self._load_patterns()

        java_files = sorted(INPUT_DIR.glob('*.java'))
        if not java_files:
            raise FileNotFoundError('input/ 폴더에 .java 파일이 없습니다.')

        OUTPUT_DIR.mkdir(exist_ok=True)
        converted: list[Path] = []

        for java_file in java_files:
            logger.info(f'변환 중: {java_file.name}')
            try:
                result = self.convert_file(java_file, patterns)

                if isinstance(result, dict):
                    out_java = OUTPUT_DIR / java_file.name
                    out_java.write_text(result['java'], encoding='utf-8')
                    converted.append(out_java)
                    logger.info(f'완료 (Java): {out_java}')

                    if result.get('xml'):
                        out_xml = OUTPUT_DIR / self._mapper_name(java_file.stem)
                        out_xml.write_text(result['xml'], encoding='utf-8')
                        converted.append(out_xml)
                        logger.info(f'완료 (Mapper XML): {out_xml}')
                else:
                    out_path = OUTPUT_DIR / java_file.name
                    out_path.write_text(result, encoding='utf-8')
                    converted.append(out_path)
                    logger.info(f'완료: {out_path}')

            except Exception as e:
                logger.error(f'실패 ({java_file.name}): {e}')

        return converted
