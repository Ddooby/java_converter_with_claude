# EJB → Spring Java Converter (with Claude)

## 프로젝트 개요

EJB(Enterprise Java Beans) 소스를 Spring Framework Java 소스로 자동 변환하는 Python 기반 컨버터.
Claude API를 활용해 AS-IS / TO-BE 샘플 코드 쌍으로부터 변환 패턴을 학습하고, 이를 바탕으로 신규 EJB 파일을 Spring 코드로 변환한다.

### 기술 스택

| 항목 | 내용 |
|------|------|
| 언어 | Python 3.11+ |
| AI | Claude API (claude-sonnet-4-6) |
| AS-IS | EJB + MiPlatform / Oracle / JEUS 8 / OpenJDK 1.8 |
| TO-BE | Spring Framework 5.3.27 + Nexacro / Oracle / Tomcat 9.0 / OpenJDK 17 |

---

## 폴더 구조

```
java_converter_with_claude/
├── CLAUDE.md                   # 이 파일
├── README.md
├── requirements.txt            # Python 의존성
├── .env.example                # 환경변수 템플릿
├── .env                        # 실제 환경변수 (git 제외)
│
├── converter/                  # 핵심 변환 로직
│   ├── __init__.py
│   ├── main.py                 # CLI 진입점
│   ├── analyzer.py             # 샘플 패턴 분석기
│   ├── converter.py            # 변환 실행기
│   ├── claude_client.py        # Claude API 클라이언트
│   └── utils.py                # 공통 유틸리티
│
├── samples/                    # 패턴 학습용 샘플 코드
│   ├── as_is/                  # AS-IS EJB 원본 파일
│   └── to_be/                  # TO-BE Spring 변환 파일
│       (파일명은 as_is와 동일하게 맞춰야 함)
│
├── input/                      # 변환할 EJB 소스 파일 (사용자 입력)
├── output/                     # 변환된 Spring 소스 파일 (자동 생성)
├── patterns/                   # 학습된 변환 패턴 캐시 (JSON)
└── tests/
    ├── __init__.py
    └── test_converter.py
```

---

## 워크플로우

```
1. 샘플 준비
   samples/as_is/  ← AS-IS EJB 파일 배치
   samples/to_be/  ← TO-BE Spring 파일 배치 (같은 파일명)

2. 패턴 학습
   python -m converter.main learn

3. 변환 실행
   input/ ← 변환할 EJB 파일 배치
   python -m converter.main convert

4. 결과 확인
   output/ ← 변환된 Spring 파일 확인
```

---

## 주요 변환 규칙 (EJB → Spring)

### 어노테이션 변환
| AS-IS (EJB) | TO-BE (Spring) |
|-------------|----------------|
| `@Stateless` | `@Service` |
| `@Stateful` | `@Service` + 상태 관리 |
| `@EJB` | `@Autowired` |
| `@PersistenceContext` | `@Autowired` (Repository) |
| `@TransactionAttribute` | `@Transactional` |
| `@Remote`, `@Local` | 제거 (인터페이스 단순화) |

### 클래스 구조 변환
| AS-IS (EJB) | TO-BE (Spring) |
|-------------|----------------|
| SessionBean implements SessionBeanInterface | `@Service` 클래스 |
| Home Interface / Remote Interface | 제거 |
| JNDI Lookup | `@Autowired` DI |
| Container-Managed Transaction | `@Transactional` |

### 패키지/임포트 변환
| AS-IS | TO-BE |
|-------|-------|
| `javax.ejb.*` | `org.springframework.*` |
| `javax.naming.*` | 제거 |
| `com.tmax..*` (JEUS) | Spring 대응 클래스 |

---

## 개발 가이드

### 환경 설정
```bash
pip install -r requirements.txt
cp .env.example .env
# .env 에 ANTHROPIC_API_KEY 입력
```

### Claude API 사용 원칙
- 모델: `claude-sonnet-4-6` (기본값)
- 샘플 코드는 프롬프트에 직접 포함 (few-shot learning)
- 변환 패턴은 `patterns/` 에 JSON으로 캐싱하여 API 호출 최소화
- 대용량 파일은 청크(chunk) 단위로 분할 처리

### 코드 스타일
- Python 타입 힌트 적극 사용
- 클래스 단위 모듈화
- 에러는 명시적으로 raise, 조용히 무시하지 않음
- 로그는 print 대신 `logging` 모듈 사용

---

## 샘플 파일 네이밍 규칙

샘플은 AS-IS / TO-BE 파일명을 **동일하게** 맞춰야 패턴 매핑이 가능하다.

```
samples/
  as_is/
    UserService.java       # EJB 원본
    OrderBean.java
  to_be/
    UserService.java       # Spring 변환본 (같은 파일명)
    OrderBean.java
```

---

## 주의사항

- `.env` 파일은 절대 git에 커밋하지 않음 (`.gitignore` 처리)
- `ANTHROPIC_API_KEY` 는 반드시 `.env` 또는 환경변수로만 관리
- `output/` 폴더 내용은 git 추적 제외 권장
