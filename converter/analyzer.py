import json
import logging
from pathlib import Path

from .claude_client import ClaudeClient

logger = logging.getLogger(__name__)

SAMPLES_AS_IS = Path("samples/as_is")
SAMPLES_TO_BE = Path("samples/to_be")
PATTERNS_FILE = Path("patterns/learned_patterns.json")

SYSTEM_PROMPT = """당신은 EJB → Spring Framework 마이그레이션 전문가입니다.
AS-IS(EJB)와 TO-BE(Spring) 코드 쌍을 분석하여 변환 패턴을 JSON 형식으로 추출하세요.

출력 형식:
{
  "annotation_mappings": {"@EJB어노테이션": "@Spring어노테이션"},
  "import_mappings": {"javax.ejb.패키지": "org.springframework.패키지"},
  "class_structure_rules": ["규칙 설명"],
  "method_patterns": ["메서드 변환 패턴 설명"],
  "general_rules": ["기타 변환 규칙"]
}

JSON 외 다른 텍스트는 출력하지 마세요."""


class PatternAnalyzer:
    def __init__(self):
        self.client = ClaudeClient()

    def load_samples(self) -> list[dict]:
        """AS-IS/TO-BE 샘플 파일을 쌍으로 로드."""
        pairs = []
        for as_is_file in sorted(SAMPLES_AS_IS.glob("*.java")):
            to_be_file = SAMPLES_TO_BE / as_is_file.name
            if not to_be_file.exists():
                logger.warning(f"TO-BE 샘플 없음: {as_is_file.name} — 건너뜀")
                continue
            pairs.append({
                "filename": as_is_file.name,
                "as_is": as_is_file.read_text(encoding="utf-8"),
                "to_be": to_be_file.read_text(encoding="utf-8"),
            })
        return pairs

    def analyze(self) -> dict:
        """샘플로부터 변환 패턴을 학습하고 JSON으로 저장."""
        pairs = self.load_samples()
        if not pairs:
            raise FileNotFoundError("samples/as_is/ 에 .java 샘플 파일이 없습니다.")

        logger.info(f"{len(pairs)}개 샘플 쌍으로 패턴 분석 시작")

        sample_text = ""
        for p in pairs:
            sample_text += f"\n\n--- 샘플: {p['filename']} ---\n"
            sample_text += f"[AS-IS]\n{p['as_is']}\n"
            sample_text += f"[TO-BE]\n{p['to_be']}\n"

        user_prompt = f"다음 AS-IS/TO-BE 샘플 쌍을 분석하여 변환 패턴을 추출하세요:{sample_text}"
        raw = self.client.send(SYSTEM_PROMPT, user_prompt)

        patterns = json.loads(raw)
        PATTERNS_FILE.parent.mkdir(exist_ok=True)
        PATTERNS_FILE.write_text(json.dumps(patterns, ensure_ascii=False, indent=2), encoding="utf-8")
        logger.info(f"패턴 저장 완료: {PATTERNS_FILE}")
        return patterns

    def load_patterns(self) -> dict:
        """저장된 패턴을 로드. 없으면 analyze() 실행."""
        if PATTERNS_FILE.exists():
            return json.loads(PATTERNS_FILE.read_text(encoding="utf-8"))
        logger.info("저장된 패턴 없음 — 새로 분석합니다.")
        return self.analyze()
