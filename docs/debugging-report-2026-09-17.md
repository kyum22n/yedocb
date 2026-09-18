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

---

## 2026-09-18 추가 디버깅 — 아이디/비밀번호 찾기 구현 및 실제 미작동 기능 수정

### 배경

사용자가 "아이디/비밀번호 찾기가 아직 실행되지 않는다"고 지적하며, 문서-코드 불일치뿐 아니라
**실제로 동작하지 않는 기능**이 더 있는지 재점검을 요청했다. 백엔드/프론트엔드를 재조사한 결과,
`docs/test-report.md`에 이미 기록되어 있던 미해결 버그 1건(통계 LEFT JOIN)이 여전히 재현되는
것과, 별도로 여러 프론트엔드 화면에서 "버튼을 눌러도 실패 시 아무 반응이 없는" 실제 버그를
새로 발견했다. 카테고리 등록 SQL 버그(과거 test-report.md 이슈 #1)는 이미 배포 후 디버깅에서
고쳐져 있었음을 재확인했다(문서만 낡아 있었음, `docs/test-report.md`/`deployment-migration.md`에
반영 완료).

### E. [신규 기능] 아이디/비밀번호 찾기 — 이메일(SMTP) 기반 구현

사용자가 SMTP 계정을 보유하고 있어 실제로 동작하도록 구현했다(기존에는 백엔드 엔드포인트
자체가 없어 프론트가 항상 "지원되지 않습니다" alert만 띄우는 스텁이었음).

**백엔드 (yedocb)**
- `dto/request/user/UserFindIdRequestDto.java`, `UserFindPasswordRequestDto.java` 신규
- `service/MailService.java` 신규 — `JavaMailSender` 기반 아이디 안내/임시 비밀번호 메일 발송
- `service/UserService.java` — `findIdAndSendEmail()`, `resetPasswordAndSendEmail()` 추가.
  **보안 설계**: 이메일/아이디가 실제로 존재하지 않아도 예외를 던지지 않고 조용히 종료 —
  존재 여부에 따라 응답이 달라지면 이메일/아이디 열거 공격에 노출되므로, 항상 동일한 200
  응답을 반환하도록 설계함. 임시 비밀번호는 `SecureRandom` 기반으로 영문+숫자+특수문자를
  모두 포함하도록 생성(User 엔티티의 비밀번호 복잡도 규칙과 동일).
- `controller/UserController.java` — `POST /api/user/find-id`, `POST /api/user/find-password`
  추가 (둘 다 permitAll)
- `security/SecurityPaths.java` — 위 두 경로를 `PUBLIC_PATTERNS`에 추가
- `application.properties` — `spring.mail.*` 설정 추가. `MAIL_HOST`(기본값 `smtp.gmail.com`),
  `MAIL_PORT`(기본값 587), `MAIL_USERNAME`/`MAIL_PASSWORD`(기본값 빈 문자열 — 기존 OAuth
  시크릿과 동일한 패턴, 실제 값은 절대 코드/문서에 직접 쓰지 않고 환경변수로만 주입)
- `docs/api-contract.md` — 두 엔드포인트 문서화, 배포 전 `MAIL_*` 환경변수 필요성 명시
- 테스트: `UserServiceTest`(신규 4개, Mockito 단위테스트 — **통과 확인함**), `UserControllerTest`
  (신규 4개, MockMvc — Testcontainers 필요해 **이번 세션에서 실행 확인 못함**)

**프론트엔드 (yedocf)**
- `src/pages/user/FindAccountPage.jsx` — `handleFindId`/`handleFindPassword`가 실제로
  `axiosInstance.post("/api/user/find-id" | "/api/user/find-password", ...)`를 호출하도록 변경.
  UX도 변경: 백엔드가 존재 여부를 노출하지 않으므로, 화면에도 찾은 아이디/임시비밀번호를
  직접 표시하지 않고 "등록된 회원이 있다면 이메일로 발송했습니다" 형태의 안내만 표시
- `src/pages/user/FindAccountPage.test.jsx` 신규 — 5개 테스트, **전부 통과**

**⚠️ 배포 전 필수 확인**: Render 콘솔에 `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`
환경변수를 설정해야 실제 메일이 발송된다(Gmail 사용 시 일반 비밀번호가 아니라 **앱 비밀번호**
필요). 값 설정은 사용자가 Render 콘솔에서 직접 해야 하며, 이 세션에서는 절대 실제 SMTP
자격증명을 다루지 않았다.

### F. [버그 수정] 통계 LEFT JOIN — 날짜 필터 시 예약 0건 진료항목 누락

- `mapper/AdminStatisticsMapper.xml`의 `selectTreatmentStatistics` — 날짜 범위 조건을
  `<where>`(WHERE절)에서 `LEFT JOIN ... ON` 절로 이동. 이제 날짜 필터를 걸어도 예약이 0건인
  진료항목이 `reservation_count=0`으로 계속 포함된다.
- `mapper/AdminStatisticsMapperTest.java`의 기존 실패-재현용 테스트를
  `날짜범위지정시에도_예약없는진료항목이_통계에_0건으로_포함된다`로 갱신(성공을 기대하도록 변경)
- `docs/deployment-migration.md`, `docs/test-report.md` — 해당 이슈를 취소선 처리하고 수정 완료로 기록
- **Testcontainers 필요해 이번 세션에서 테스트 실행 확인은 못함** — 컴파일은 성공 확인함

### G. [버그 수정] 프론트엔드 관리자 화면 — 실패 시 무반응 문제

- `src/pages/admin/ReservationManagePage.jsx` — 예약 상태변경(`handleStatusChange`), 삭제
  (`handleDeleteReservation`) 실패 시 `alert`로 사용자에게 안내하도록 추가(기존엔 `console.error`만 함)
- `src/pages/admin/NoticeEventManagePage.jsx` — 공지/이벤트 등록·수정·삭제
  (`handleCreateNotice`/`handleUpdateNotice`/`handleDeleteNotice`)에 try/catch 자체가 없어 실패 시
  완전히 조용히 실패하던 것을 다른 관리자 페이지와 동일한 try/catch + alert 패턴으로 통일
- `src/pages/admin/UserManagePage.jsx`:
  - 회원 추가(`onAction`)가 `document.querySelector`로 DOM을 직접 읽는 미완성 구현이었던 것을
    `newUserForm` React state 기반 완전한 controlled input으로 전환(다른 관리자 페이지의
    `form` state 패턴과 동일하게 통일)
  - 회원 추가/삭제 실패 시 `alert` 안내 추가(기존엔 추가는 `console.error`만, 삭제는 그마저도 없었음)
- 위 3개 파일 모두 **`npm run build` 성공, 기존 Vitest 22개 통과 재확인함**(신규 테스트는
  UI 텍스트/조건 변경이 없어 별도 추가하지 않음 — 실패 처리 분기만 추가된 것이라 스냅샷성 검증
  가치가 낮다고 판단)

### 이번 라운드에서 다루지 않기로 한 항목 (사용자 확인)

- `src/pages/user/LoginPage.jsx`의 소셜 로그인 환경변수 미검증 — 배포 환경변수가 정상 설정되어
  있으면 실사용에 문제없어 이번엔 보고만 하고 수정하지 않음
- `src/pages/user/MyPage.jsx`의 세션 만료 시 무안내 — 경미한 UX 이슈로 판단, 이번엔 제외
- `src/pages/admin/NoticeEventEditPage.jsx` — 라우팅되지 않는 죽은 파일, 사용자 확인 결과 그대로 유지

### 검증 결과 요약 (2026-09-18)

| 항목 | 결과 |
|---|---|
| 백엔드 전체 컴파일(`compileJava`/`compileTestJava`) | **성공** |
| 백엔드 `UserServiceTest`(신규 4개 포함) | **전부 통과** |
| 백엔드 `AdminReservationServiceTest` | **전부 통과** (회귀 없음 재확인) |
| 백엔드 `UserControllerTest`(신규 4개), `AdminStatisticsMapperTest` | Docker 미구동으로 **미실행** |
| 프론트엔드 Vitest 전체(27개, 신규 `FindAccountPage` 5개 포함) | **전부 통과** |
| 프론트엔드 빌드(`npm run build`) | **성공** |

Docker가 여전히 복구되지 않아, 이번에 추가한 `UserControllerTest`의 신규 테스트 4개와
`AdminStatisticsMapperTest`의 갱신된 테스트는 Mockito 단위테스트 수준(서비스 로직)까지만
검증했고, 실제 Postgres/MockMvc를 통한 종단 검증은 하지 못했다. Docker 복구 후
`./gradlew cleanTest test`로 전체 재확인이 필요하다.
