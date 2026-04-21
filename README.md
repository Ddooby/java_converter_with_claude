# java_converter_with_claude

# 마이그레이션 기술 스펙

## AS-IS

| 항목 | 내용 |
|------|------|
| 프레임워크 | EJB (Enterprise Java Beans), MiPlatform |
| DB | Oracle |
| WAS | JEUS 8 |
| JDK | OpenJDK 1.8 |
| 형상관리 | SVN / Git (프로젝트 수행 시에만) |

---

### EJB(Enterprise Java Beans) 개요

**Java Bean**이란 Java로 작성된 소프트웨어 컴포넌트를 말한다. Java는 프로그램 기본 단위가 클래스이고, Java Bean은 그 클래스들이 복합적으로 이루어진 구조다. Java Bean은 **데이터를 표현하는 것을 목적**으로 하는 자바 클래스로, 컴포넌트와 비슷한 의미로 사용된다.

**EJB**는 시스템을 구현하기 위한 **서버 측 컴포넌트 모델**이다.

- Application 업무 로직을 갖고 있는 서버 Application이다.
- 비즈니스 객체들을 관리하는 컨테이너 기술, 설정에 의한 트랜잭션 기술이 담겨있다.
- 2000년대 초반 Java 진영에서 표준으로 인정한 기술로 큰 인기를 얻었다.
- 복잡한 대규모 시스템의 **분산 객체 환경**을 쉽게 구현하기 위해 등장했다.

**EJB 구성 요소**

| 구성 요소 | 역할 |
|-----------|------|
| Enterprise Bean | 비즈니스 로직 구현 |
| Container | DB 처리, Transaction 처리 등 시스템 서비스 구현 |
| EJB Server | Enterprise Bean 및 Container 실행 환경 |
| Client Application | EJB 서비스를 호출하는 클라이언트 |

---

## TO-BE

| 항목 | 내용 |
|------|------|
| OS | Windows *(Nexacro Studio는 Windows 계열에서만 사용 가능)* |
| JDK | OpenJDK 17 |
| Spring Framework | 5.3.27 |
| WAS | Tomcat 9.0 |
| DB | Oracle |
| Nexacro SDK | 24.0.0.200 *(21버전 → 24버전: 21버전 async/await 오류로 업그레이드)* |
| IDE | IntelliJ, Nexacro Studio (Latest) |

> **Nexacro SDK 버전 변경 이유**
> 21버전에서 `async/await` 사용 시 오류가 발생하여 24버전으로 업그레이드.

---

## AS-IS vs TO-BE 비교

| 항목 | AS-IS | TO-BE |
|------|-------|-------|
| JDK | OpenJDK 1.8 | OpenJDK 17 |
| 프레임워크 | EJB + MiPlatform | Spring Framework 5.3.27 + Nexacro |
| WAS | JEUS 8 | Tomcat 9.0 |
| DB | Oracle | Oracle |
| IDE | - | IntelliJ + Nexacro Studio |