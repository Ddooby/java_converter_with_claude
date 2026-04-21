import logging
from pathlib import Path

from .analyzer import PatternAnalyzer
from .claude_client import ClaudeClient

logger = logging.getLogger(__name__)

INPUT_DIR = Path("input")
OUTPUT_DIR = Path("output")

SYSTEM_PROMPT_TEMPLATE = """당신은 EJB → Spring Framework 마이그레이션 전문가입니다.
아래 변환 패턴을 참고하여 AS-IS EJB 코드를 TO-BE Spring 코드로 변환하세요.

[학습된 변환 패턴]
{patterns}

변환 규칙:
- 완전한 Java 소스 코드만 출력하세요 (설명 없이)
- 어노테이션, 임포트, 클래스 구조를 패턴에 따라 변환하세요
- 비즈니스 로직은 변경하지 마세요
- Spring Framework 5.3.x 기준으로 작성하세요
- OpenJDK 17 문법을 사용해도 됩니다"""


class EjbConverter:
    def __init__(self):
        self.client = ClaudeClient()
        self.analyzer = PatternAnalyzer()

    def convert_file(self, source_path: Path, patterns: dict) -> str:
        """단일 EJB 파일을 Spring 코드로 변환."""
        source_code = source_path.read_text(encoding="utf-8")
        import json
        system_prompt = SYSTEM_PROMPT_TEMPLATE.format(
            patterns=json.dumps(patterns, ensure_ascii=False, indent=2)
        )
        user_prompt = f"다음 EJB 코드를 Spring으로 변환하세요:\n\n{source_code}"
        return self.client.send(system_prompt, user_prompt)

    def convert_all(self, force_relearn: bool = False) -> list[Path]:
        """input/ 폴더의 모든 .java 파일을 변환하여 output/ 에 저장."""
        if force_relearn:
            patterns = self.analyzer.analyze()
        else:
            patterns = self.analyzer.load_patterns()

        java_files = sorted(INPUT_DIR.glob("*.java"))
        if not java_files:
            raise FileNotFoundError("input/ 폴더에 .java 파일이 없습니다.")

        OUTPUT_DIR.mkdir(exist_ok=True)
        converted = []

        for java_file in java_files:
            logger.info(f"변환 중: {java_file.name}")
            try:
                result = self.convert_file(java_file, patterns)
                out_path = OUTPUT_DIR / java_file.name
                out_path.write_text(result, encoding="utf-8")
                converted.append(out_path)
                logger.info(f"완료: {out_path}")
            except Exception as e:
                logger.error(f"실패 ({java_file.name}): {e}")

        return converted
