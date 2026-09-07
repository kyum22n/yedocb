# yedocb

병원(피부/성형 클리닉) 예약 서비스의 백엔드. 팀 프로젝트 원본(`kyum2n/yedocb`)의 검증된 인증 구조와,
개인적으로 확장한 넓은 도메인 기능(상담/진료항목/통계/리뷰 등)을 하나로 재통합해 DTO/예외처리/검증/설정분리를
갖춘 구조로 재작성한 개인 포트폴리오 프로젝트입니다.

- **라이브**: https://yedocb.onrender.com (무료 tier — 일정 시간 미사용 시 첫 요청이 느릴 수 있습니다)
- **프론트엔드 저장소**: [`kyum22n/yedocf`](https://github.com/kyum22n/yedocf)

## 기술 스택

- Java 21, Spring Boot 4.0.6, Spring Security (JWT + Google/Kakao OAuth2)
- MyBatis + PostgreSQL (운영: [Neon](https://neon.tech) 서버리스 Postgres)
- Gradle, JUnit5 + Mockito + Testcontainers
- 배포: Docker 이미지로 [Render](https://render.com) Web Service에 배포 (Render는 Java를 네이티브 런타임으로 지원하지 않아 Docker 빌드로 전환)

## 아키텍처

```
Controller (@RestController) → Service (@Service) → DAO (MyBatis @Mapper) → Mapper XML (PostgreSQL)
```

- 모든 컨트롤러는 공용 응답 래퍼 없이 `ResponseEntity<정확한DTO타입>`을 그대로 반환합니다.
- 예외 처리는 `GlobalExceptionHandler`(`@RestControllerAdvice`) 하나로 집중되어 있습니다.
- 인증은 JWT 기반 Stateless. permitAll 경로는 `security/SecurityPaths` 한 곳에서만 관리합니다.
- 자세한 도메인별 파일 위치는 [`docs/architecture-after.md`](docs/architecture-after.md)(리팩토링 전 상태는 [`docs/architecture-before.md`](docs/architecture-before.md)), API 명세는 [`docs/api-contract.md`](docs/api-contract.md) 참고.

## 로컬 실행

```bash
# PostgreSQL 로컬 인스턴스 필요 (application-local.properties 참고: localhost:5432/yedocb)
./gradlew bootRun

# 테스트 (Docker 필요 — Testcontainers가 Postgres를 자동 기동)
./gradlew test
```

## 배포

AWS EC2 + nginx로 운영되던 인프라를 **Neon(Postgres) + Render(백엔드) + Vercel(프론트엔드)** 무료 인프라로 전환했습니다.
전환 과정, 겪은 트러블슈팅(Render Docker 전환, JWT_SECRET 이슈, OAuth Redirect URI 디버깅 등), 컷오버 순서는
[`docs/deployment-migration.md`](docs/deployment-migration.md)에 상세히 기록되어 있습니다.

## 리팩토링 히스토리

두 서로 다른 백엔드 구현체(팀 원본 B + 개인 확장 A)를 하나로 통합하는 과정에서 발견한 버그, 설계 결정,
트레이드오프는 [`docs/refactor-log.md`](docs/refactor-log.md)에 기록되어 있습니다.

## 알려진 제한사항

- **아이디/비밀번호 찾기, 이메일 인증**: SMTP 연동이 필요한 별도 작업이라 이번 배포 범위에서 제외했습니다. 포트폴리오 완성 후 추가 예정입니다.
- 무료 tier(Render/Neon) 특성상 일정 시간 미사용 시 첫 요청이 느릴 수 있습니다(콜드스타트).
