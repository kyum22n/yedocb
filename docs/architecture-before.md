# Architecture Before (리팩토링 착수 전)

이 문서는 통합 리팩토링(`docs/architecture-after.md`)을 시작하기 전, 두 서로 다른 백엔드 구현체가
존재하던 시점의 상태를 기록한다.

## 배경

이 프로젝트는 병원(피부/성형 클리닉) 예약 서비스이며, 실제로는 **두 개의 서로 다른 백엔드 구현체**가 있었다.

## B — 팀 프로젝트 원본 (`kyum2n/yedocb`, 읽기전용 참고용)

- 패키지 `com.example.yedocb`
- 도메인: Admin/User/Reservation/NoticeEvent/Inquiry (5개)
- **장점**: JWT + OAuth(Google/Kakao) 인증이 실제로 동작. 서비스 계층(interface + Impl)이 갖춰져 있음.
- **약점**: DTO 계층이 없어 엔티티를 그대로 응답에 노출(비밀번호 포함 가능성), CORS/로깅/설정이 코드에 하드코딩,
  `application.properties`에 로컬/운영 설정이 분리되지 않음.
- 배포: GitHub Actions가 SSH/SCP로 EC2 인스턴스에 JAR를 직접 배포, nginx가 프론트 정적 파일을 서빙.

## A — 개인 확장 버전 (로컬, 최종 산출물의 출발점)

- 패키지 `com.example.demo`
- 도메인: B의 5개 + Consultation(상담), Treatment/TreatmentCategory(진료항목), StaffSchedule(직원일정, 이후 삭제됨),
  Statistics/Dashboard(관리자 통계) — 훨씬 넓은 도메인 커버리지
- **장점**: DTO 계층이 이미 존재, 도메인이 B보다 훨씬 풍부함.
- **약점**: 인증/권한이 전혀 없음(로그인 API가 주석 처리되어 있었고 `PasswordEncoder` 빈 등록 자체가 누락되어
  구동 실패 가능성이 있었음), 일부 컨트롤러에 컴파일 오류 유발 가능한 import 누락, `AdminMemberController`의
  `GET /list`가 비밀번호를 포함한 엔티티를 그대로 반환, `Review`는 엔티티만 있고 서비스/컨트롤러 미구현.

## 프론트엔드 (`kyum2n/yedocf` 참고, 팀 공유본과는 별개)

- axios 인스턴스가 3개로 분산(`api/axiosInstance.js`, `lib/axios.js`, `api/publicAxios.js`), `withCredentials` 사용
- 라우트 가드(`ProtectedRoute`/`AdminRoute`) 부재 — 비로그인 상태에서도 보호되어야 할 페이지 접근 가능
- 예약 항목 선택이 하드코딩된 `itemMap`/드롭다운
- 로그인 토큰/카카오 키 등을 그대로 출력하는 디버그 `console.log`가 다수 존재

## 인프라

- **컴퓨트**: AWS EC2 인스턴스 1대에 백엔드(JAR 상시 프로세스)와 nginx(프론트 정적 서빙)가 함께 배포
- **DB**: EC2 인스턴스 내 PostgreSQL (관리형 DB 미사용)
- **CI/CD**: GitHub Actions가 매 push마다 SSH 키(`secrets.YEDOC`)로 EC2에 직접 접속해 JAR/정적 파일을 교체
  (`docs/refactor-log.md` §10에 워크플로우 원문 보존)
- **시크릿 관리**: OAuth client secret이 빌드 시점에 `application.properties`에 평문으로 append되어 JAR에 임베드,
  프론트 워크플로우는 `VITE_GOOGLE_CLIENT_SECRET`을 `.env`에 넣어 클라이언트 번들에 노출될 수 있는 위험한 패턴이 있었음

## 이후 진행

이 두 구현체를 하나로 합쳐 DTO/예외처리/검증/설정분리를 갖춘 백엔드로 재구성한 과정은
`docs/architecture-after.md`와 `docs/refactor-log.md`에, AWS 인프라를 Neon/Render/Vercel로 전환한 과정은
`docs/deployment-migration.md`에 기록되어 있다.
