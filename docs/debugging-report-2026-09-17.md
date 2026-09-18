# 디버깅 계획 및 결과 — 관리자 권한 분리 & 예약 상태 전이 검증 (2026-09-17)

> 이 문서는 포트폴리오(Notion/PDF) vs 실제 코드 대조 검증(1단계)에서 발견된 문제들에 대한
> 디버깅 계획을 먼저 기록하고, 구현 완료 후 결과를 추가로 갱신하는 방식으로 작성한다.

## 배경

1단계 검증 결과, "최고 관리자(SUPERADMIN)만 관리자 계정을 등록·삭제할 수 있다"는 포트폴리오의
설계 의도가 리팩토링 과정에서 **프론트엔드 UI(사이드바 메뉴 숨김) 수준으로만 구현되고 백엔드에는
전혀 강제되지 않은 채** 남아있는 것이 확인되었다. 백엔드는 `/admin/**` 전체를 ADMIN과 SUPERADMIN을
동일하게 취급하는 단일 규칙(`hasAnyRole("ADMIN","SUPERADMIN")`)으로만 보호하고 있어, 일반 ADMIN
계정도 API를 직접 호출하면 다른 관리자 계정을 등록·삭제할 수 있는 상태였다.

문서(포트폴리오)가 원래 의도한 올바른 설계이므로, 문서를 기준으로 코드를 맞춘다.

## 범위

### A. [P1] 관리자 권한 분리 — 백엔드 실제 강제 구현

**백엔드 (yedocb)**
- `SecurityConfig.java`에 `@EnableMethodSecurity` 추가
- `AdminController.registerAdmin()`(`POST /admin/register`), `deleteAdmin()`(`DELETE /admin/delete/{adminId}`)에
  `@PreAuthorize("hasRole('SUPERADMIN')")` 추가
- `PUT /admin/update`는 범위 제외 (문서가 등록·삭제만 SUPERADMIN 전용이라고 명시)

**테스트**
- `AdminControllerTest.java`: 기존 등록/삭제 성공 테스트를 SUPERADMIN 토큰으로 변경, ADMIN 토큰
  시도 시 403이 되는 신규 테스트 추가

**문서**
- `docs/api-contract.md`, `docs/architecture-after.md`에 SUPERADMIN 전용 강제 사실 명시

**⚠️ 배포 전 확인 필요**: 운영 DB(Neon)에 `adminRole='SUPERADMIN'` 계정이 최소 1개 존재해야 함 —
없으면 배포 즉시 아무도 새 관리자를 등록할 수 없다. 코드로 해결 불가, 운영 DB 데이터 작업 필요.

**프론트엔드 (yedocf)**
- `StaffManagePage.jsx`: 등록/삭제 버튼을 `sessionStorage.getItem("role") === "SUPERADMIN"`일 때만 렌더링
- `/admin/staff` 라우트에 SUPERADMIN 전용 가드 추가

### B. [P2] 예약 상태 전이 검증 로직 추가

`AdminReservationService.modifyReservationStatus()`에 상태 전이 유효성 검증 추가:
- `PENDING` → `CONFIRMED`, `CANCELED`
- `CONFIRMED` → `COMPLETED`, `CANCELED`, `NO_SHOW`
- `COMPLETED`/`CANCELED`/`NO_SHOW` → 종결 상태(추가 전이 불가)

허용되지 않는 전이 시 기존 `IllegalArgumentException` 재사용(400).

### C. [P3] 문서 정리 (코드 변경 없음)

- `docs/api-contract.md`: 삭제된 `/admin/staff-schedules` 예시 제거
- `docs/test-report.md`: 이번 변경 반영해 최신화
- `yedocf/README.md`: 이메일 인증 화면 표현 정정, "직원 관리" 화면 언급 추가
- (리포지토리 밖) Notion/PDF 포트폴리오 정정 필요 항목 — 사용자 별도 조치 필요:
  - "문의 답변 자동 이메일 발송" 문구 삭제
  - "백엔드 193개 테스트" 수치 갱신
  - 성형외과 프로젝트 개발 기간/PM 역할 표기를 Notion·PDF 간 일치

### D. 구현 완료 후 전체 검증

1. 백엔드 전체 테스트 (`./gradlew cleanTest test`)
2. 프론트엔드 전체 테스트 (`npm test`) + 빌드 (`npm run build`)
3. 수동 기능 검증 (로컬 실행): SUPERADMIN/ADMIN 권한 분리 시나리오, 예약 상태 전이 시나리오,
   기존 핵심 기능 회귀 확인(로그인/예약/상담전환/리뷰/공지)
4. 결과를 본 문서 하단에 "구현 및 검증 결과" 섹션으로 추가

이번 범위에서 제외한 항목: 관리자 정보 수정(`PUT /admin/update`) 권한 제한, 죽은 코드 정리
(`AdminLoginService.java`, `AdminAuthDao.java`, `InvalidVerificationCodeException`), 리포지토리
밖 Notion/PDF 문서 직접 수정.

---

## 구현 및 검증 결과

### 변경 파일 목록

**A. 관리자 권한 분리**
- `src/main/java/com/example/demo/config/SecurityConfig.java` — `@EnableMethodSecurity` 추가
- `src/main/java/com/example/demo/controller/AdminController.java` — `registerAdmin()`,
  `deleteAdmin()`에 `@PreAuthorize("hasRole('SUPERADMIN')")` 추가
- `src/test/java/com/example/demo/controller/AdminControllerTest.java` — 등록/삭제 성공 테스트를
  `superAdminToken()` 기준으로 변경, `ADMIN권한으로_관리자등록시도하면_403()` /
  `ADMIN권한으로_관리자삭제시도하면_403()` 신규 추가
- `docs/api-contract.md`, `docs/architecture-after.md` — SUPERADMIN 전용 강제 사실과 배포 전
  운영 DB SUPERADMIN 계정 필요성 명시
- (yedocf) `src/pages/admin/StaffManagePage.jsx` — "관리자 추가" 버튼, 삭제 컬럼/버튼을
  `sessionStorage.getItem("role") === "SUPERADMIN"`일 때만 렌더링하도록 변경
- (yedocf) `src/components/auth/AdminRoute.jsx` — `requireSuperAdmin` prop 추가(SUPERADMIN이
  아니면 `/admin`으로 리다이렉트)
- (yedocf) `src/App.jsx` — `/admin/staff` 라우트를 `<AdminRoute requireSuperAdmin>`로 감쌈
- (yedocf) `src/components/auth/AdminRoute.test.jsx` — `requireSuperAdmin` 관련 테스트 2개 추가

**B. 예약 상태 전이 검증**
- `src/main/java/com/example/demo/service/AdminReservationService.java` —
  `ALLOWED_RESERVATION_STATUS_TRANSITIONS` 맵 기반 전이 검증 추가
- `src/test/java/com/example/demo/service/AdminReservationServiceTest.java` (신규) — 정상/비정상
  전이 테스트 6개
- `docs/api-contract.md` — 예약 상태 전이 규칙 표 추가

**C. 문서 정리**
- `docs/api-contract.md` — 삭제된 `/admin/staff-schedules` 예시를 `/admin/consultations`로 교체
- `docs/test-report.md` — Phase 6 스냅샷임을 명시, StaffSchedule 관련 행 제거, 2026-09-17 갱신
  섹션 추가
- (yedocf) `README.md` — 이메일 인증 화면 관련 표현 정정("화면은 있으나 비활성화" →
  "이메일 인증 화면은 구현되어 있지 않음"), "직원 관리" 화면 항목 추가

**환경 설정(이번 세션 로컬 환경 문제 대응)**
- `build.gradle` — test 태스크에 `jvmArgs '-XX:+EnableDynamicAgentLoading', '-Xmx512m'` 추가.
  이 머신의 JDK 21.0.8에서 Mockito의 self-attach가 기본적으로 차단되어 있어 필요했던 설정으로,
  사용자 확인 후 유지하기로 결정함(임시 해결책이 아니라 이 환경에서 테스트 실행에 실질적으로
  필요한 설정).

### 실행한 검증과 결과

| 검증 항목 | 결과 |
|---|---|
| 백엔드 `AdminReservationServiceTest` (신규 6개, 상태 전이 검증) | **통과** (`./gradlew test --tests "*AdminReservationServiceTest*"`) |
| 백엔드 전체 컴파일 (`compileJava`, `compileTestJava`) | **성공** — 코드 변경으로 인한 컴파일 오류 없음 |
| 프론트엔드 Vitest 전체 (기존 20개 + 신규 `AdminRoute` 2개 = 22개) | **전부 통과** |
| 프론트엔드 빌드 (`npm run build`) | **성공** |

### 검증하지 못한 항목 (Docker Desktop 자체 결함으로 보류)

이 세션 동안 작업 PC의 Docker Desktop이 구동되지 않았다. 처음에는 Windows 커널 레벨에서
잠긴 소켓 파일(`AppData\Local\Docker\run\dockerInference`, AF_UNIX reparse point, `fsutil`로도
접근 거부·오류 1920)이 원인으로 보여 재부팅을 시도했으나, **재부팅 직후에도 동일한 파일이
곧바로 다시 깨진 상태로 재생성되며 `Docker Desktop is unable to start` 오류가 재발**했다.
즉 일회성 잔재물 문제가 아니라 이 Docker Desktop 설치에 포함된 "Model Runner/Inference"(AI)
기능 자체의 결함으로 판단된다(설정에서 이 기능을 끄는 옵션도 노출되어 있지 않았음). 사용자
확인 결과, 이번 세션은 **Docker 없이 가능한 범위까지만 검증하고 마무리**하기로 했다. Docker를
복구(재설치/초기화 등)한 뒤 아래 항목은 **사용자가 직접 실행해서 확인이 필요하다**:

1. **백엔드 통합테스트(Testcontainers 필요)**
   ```bash
   ./gradlew cleanTest test
   ```
   특히 `AdminControllerTest`의 신규 테스트 4개(SUPERADMIN 성공 2개, ADMIN 403 2개)와
   `SecurityConfigIntegrationTest` 등 기존 전체 테스트가 회귀 없이 통과하는지 확인
   — **주의**: 이 리포트의 "발견된 이슈 #3"(커스텀 `AccessDeniedHandler` 부재로 인해 MockMvc는
   403, 실제 서버는 401을 반환하는 기존 알려진 차이)이 이번에 추가한 `@PreAuthorize` 기반
   거부에도 동일하게 나타날 수 있다 — 만약 `ADMIN권한으로_관리자등록시도하면_403()` 테스트가
   MockMvc에서는 통과하더라도, 실제 배포 서버에서 curl/Postman으로 확인하면 403이 아니라
   401이 반환될 수 있음에 유의(둘 다 "거부됨"이라는 결과는 동일하므로 보안 목적은 달성되지만,
   상태 코드가 문서와 다를 수 있음).

2. **수동 기능 검증** (로컬 `./gradlew bootRun` + `npm run dev`) — README에 따르면 `bootRun`도
   PostgreSQL 로컬 인스턴스가 필요하다(`application-local.properties`: `localhost:5432/yedocb`).
   Docker가 복구되지 않는 경우, PostgreSQL을 네이티브로 설치하거나 다른 방식(WSL 내부에
   docker-ce 직접 설치 등)으로 DB를 준비해야 한다.
   - SUPERADMIN 계정 로그인 → 직원 관리 메뉴 노출, 관리자 등록/삭제 정상 동작
   - ADMIN 계정 로그인 → 사이드바에 직원 관리 메뉴 미노출, `/admin/staff` 직접 URL 접근 시
     `/admin`으로 리다이렉트되는지 확인
   - 예약 상태 정상 전이(PENDING→CONFIRMED→COMPLETED 등)와 비정상 전이(COMPLETED→PENDING 등
     역행) 시도 시 각각 성공/에러 확인
   - 회귀 확인: 로그인, 예약 생성, 상담→예약 전환, 리뷰, 공지 노출 등 기존 핵심 기능

3. **⚠️ 배포 전 필수 확인**: 운영 DB(Neon)에 `adminRole='SUPERADMIN'` 계정이 최소 1개
   존재하는지 반드시 확인. 없다면 이번 변경 배포 즉시 아무도 새 관리자를 등록할 수 없다 —
   기존 관리자 중 한 명을 DB에서 직접 `SUPERADMIN`으로 승격시켜야 한다.

### 이번 범위에서 다루지 않은 항목

- 관리자 정보 수정(`PUT /admin/update`) 권한 제한 — 문서가 등록·삭제만 언급하여 범위 제외
- 죽은 코드 정리(`AdminLoginService.java` 전체 주석 처리, `AdminAuthDao.java` 고아 클래스,
  `InvalidVerificationCodeException` 미사용 예외) — 권한 분리 버그와 무관한 별도 정리 작업이라 제외
- 리포지토리 밖 Notion/PDF 포트폴리오 문서 직접 수정 — 이 세션에서 접근 불가, 사용자가 별도로
  다음 항목을 반영해야 함:
  - "문의 답변 등록 시 이메일 자동 발송" 문구 삭제(실제 미구현)
  - "백엔드 193개 테스트" 수치 갱신(현재는 이전 세션 기준 182개, 이번 세션에서 6개 추가)
  - 성형외과 프로젝트 개발 기간(Notion "2025.05.19~06.20" vs PDF "2025.06~07")과 PM 역할 여부
    표기를 Notion·PDF 간 일치

### 세션 종료 시점 상태 (2026-09-18)

코드 변경(A, B, C)은 모두 완료되었고, Docker 없이 가능한 범위(프론트엔드 전체 테스트/빌드,
백엔드 신규 단위테스트, 백엔드 전체 컴파일)의 검증도 모두 통과했다. 사용자 PC의 Docker
Desktop이 재부팅 후에도 자체 결함(Model Runner/Inference 컴포넌트)으로 구동되지 않아,
Testcontainers 기반 백엔드 통합테스트와 실제 서버 기동을 통한 수동 기능 검증은 **이번
세션에서 완료하지 못했다**. 사용자 확인 결과 이 상태로 세션을 마무리하기로 했다 — Docker를
복구(재설치/초기화 또는 네이티브 PostgreSQL 대체)한 뒤 위 "검증하지 못한 항목" 절의 절차를
직접 실행해 최종 확인해야 한다.
