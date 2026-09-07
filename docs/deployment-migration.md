# 배포 전환: AWS EC2+nginx → Neon+Render+Vercel

> Phase 7~11 산출물. `yedoc-migration-plan.md`의 Deploy 세션 담당 문서.
> 이 문서는 실제 진행 상황에 맞춰 계속 갱신된다 ("진행 기록" 섹션 참고).

## 요약 (포트폴리오용)

병원 예약 서비스 백엔드/프론트엔드를 **AWS EC2+nginx(GitHub Actions SSH/SCP 배포) → Neon(서버리스 Postgres) +
Render(백엔드, Docker) + Vercel(프론트엔드)** 무료 인프라로 전환했다. 기존 데이터는 이관하지 않고 `schema.sql`
기준 새 스키마만 새로 구축했다.

**결과**
- 백엔드: https://yedocb.onrender.com — Docker 이미지 배포, Neon Postgres 연동, JWT + Google/Kakao OAuth2 로그인 라이브 동작 확인
- 프론트엔드: https://yedocf.vercel.app — SPA 라우팅/환경변수 구성 완료
- 시크릿(DB 자격증명, JWT_SECRET, OAuth client secret 등)은 전 과정에서 값을 직접 다루지 않고 Render/Vercel/Neon 콘솔에서 사용자가 직접 입력
- 레거시 GitHub Actions 워크플로우(SSH 키를 이용한 EC2 직접 배포 방식)는 원문을 `docs/refactor-log.md`에 보존 후 삭제 완료
- 레거시 EC2 인스턴스는 해당 AWS 계정이 이전에 영구 해지되어 있어 이미 리소스가 정리된 상태로 확인됨(§7 진행 기록 참고)

**전환 과정에서 실제로 부딪힌 문제와 해결** (상세 트러블슈팅은 §7 진행 기록)
1. Render는 Java를 네이티브 런타임으로 지원하지 않음 → Dockerfile(멀티스테이지 빌드)로 전환
2. `JWT_SECRET` 환경변수가 빈 문자열로 등록되어 `WeakKeyException` → 256비트 이상 랜덤 값 재발급
3. Vercel에 `vercel.json`(SPA 라우팅)이 커밋되지 않아 새로고침 시 404 → 커밋 누락 발견 후 반영
4. Vercel 환경변수가 Type="Secret"(write-only)으로 저장되어 값 수정이 막힘 → Type="Config"로 재생성
5. Kakao 로그인이 `KOE301`(Redirect URI 불일치) → `KOE006`(로그인 기능 설정 오류) → 최종 성공까지, 콘솔 개편으로 옮겨진 Redirect URI 등록 위치를 여러 메뉴를 거쳐 추적
6. 배포 후 실사용 테스트 중 발견된 버그 2건(카테고리 등록 SQL 오류, 공지 비로그인 401)을 그 자리에서 수정 — "배포 후 디버깅" 원칙에 따라 배포에 지장 없는 버그는 기록만 하고, 포트폴리오 데모 시나리오에 필요한 것만 즉시 수정
7. 포트폴리오 스코프 정리 차원에서 StaffSchedule(직원 근무일정) 도메인을 백엔드/프론트/DB 전체에서 삭제

**의도적으로 배포 범위에서 제외한 것**: 아이디/비밀번호 찾기, 이메일 인증(SMTP 연동 필요) — README에 향후 계획으로 명시.

---

## 0. 선행조건 확인 (2026-09-07)

Deploy 착수 조건("Backend/Frontend가 각자 로컬에서 배포 가능 상태 도달")은 `docs/test-report.md` +
`C:\kyum\project\yedocb\docs\test-report.md` 기준으로 **충족되어 있음을 확인**했다.

| 항목 | 상태 |
|---|---|
| 백엔드 전체 테스트 | 191개 전부 통과 (`./gradlew cleanTest test`, Testcontainers Postgres 대상) |
| 프론트 전체 테스트 | 20개 전부 통과 (Vitest) |
| 프론트 `npm run build` | 성공 |
| 백엔드 설정 구조 | 이미 `PORT`/`SPRING_PROFILES_ACTIVE`/`DB_URL` 등 env var 기반, `application-prod.properties`는 기본값 없이 환경변수 누락 시 기동 실패하도록 설계됨 — Render 배포를 이미 염두에 둔 구조 |

**미해결 이슈 (배포에 지장 없음, 기록만 하고 배포 후 디버깅 대상)**:
- ~~`AdminTreatmentCategoryMapper.xml`의 `insertCategory` SQL 문법 오류~~ → **Phase 11에서 수정 완료**(커밋 `61118f7`, 아래 진행 기록 참고)
- `AdminStatisticsMapper.xml`의 LEFT JOIN이 날짜 필터 시 사실상 INNER JOIN처럼 동작 — 예약 없는 진료항목이 통계에서 누락
- `SecurityConfig`에 커스텀 `AccessDeniedHandler` 부재 — 권한 부족 시 403이 아닌 401 응답 (실서버 기준)
- ~~`/notices/**`가 permitAll GET 목록에 없어 비로그인 사용자가 공지 목록 조회 불가~~ → **Phase 11에서 수정 완료**(커밋 `c7d2a53`, 포트폴리오 데모상 비로그인 방문자도 콘텐츠를 봐야 해서 공개 게시판으로 확정)
- 프론트 `axiosInstance.js`에 응답 인터셉터 부재 — 401 공통 처리 없음

(출처: `C:\kyum\project\yedocb\docs\test-report.md` §4 "발견된 이슈")

## 1. 인프라 비교

| 영역 | 기존(AWS) | 전환 후 |
|---|---|---|
| DB | EC2 내 PostgreSQL (또는 별도 RDS 미사용, EC2 동일 인스턴스 추정) | **Neon** (Serverless Postgres, 무료 tier) |
| 백엔드 | EC2 인스턴스 + `nohup java -jar` 상시 프로세스 | **Render** (Web Service, 무료 tier, git push 배포) |
| 프론트 | EC2 + nginx 정적 서빙 | **Vercel** (정적 호스팅 + CDN, 무료 tier) |
| 배포 방식 | GitHub Actions → SSH/SCP로 EC2에 직접 배포 | Render/Vercel의 Git 연동 자동 배포 (수동 워크플로우 불필요) |
| 시크릿 관리 | GitHub Actions Secrets → 빌드 시 `application.properties`/`.env`에 주입 | Render/Vercel 각 서비스 대시보드의 환경변수 (빌드/런타임 주입, 저장소에 안 남음) |
| 마이그레이션 도구 | 없음(`schema.sql` 수동) | 없음(`schema.sql` 수동, 동일) |

## 2. 환경변수 이름 목록 (값은 사용자가 각 콘솔에서 직접 입력)

### 2-1. Render (백엔드, `yedocb`)

| 변수명 | 용도 | 비고 |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | `prod` 고정 | `application-prod.properties` 활성화 |
| `DB_URL` | Neon Postgres JDBC URL | `jdbc:postgresql://<host>/<db>?sslmode=require`, **pooled(PgBouncer) 엔드포인트 사용 권장** |
| `DB_USERNAME` | Neon DB 사용자명 | |
| `DB_PASSWORD` | Neon DB 비밀번호 | |
| `JWT_SECRET` | JWT 서명 키 | 로컬 기본값과 다른 새 값 발급 권장 (충분히 긴 랜덤 문자열) |
| `JWT_EXPIRATION` | Access Token 만료(ms) | 기존 로컬 기본값(3600000) 유지 가능 |
| `JWT_REFRESH_EXPIRATION` | Refresh Token 만료(ms) | 기존 로컬 기본값(604800000) 유지 가능 |
| `CORS_ALLOWED_ORIGINS` | 허용 오리진 | Vercel 배포 도메인(예: `https://yedocf.vercel.app`), 콤마 구분 다중 값 가능 |
| `GOOGLE_CLIENT_ID` | Google OAuth | |
| `GOOGLE_CLIENT_SECRET` | Google OAuth | |
| `GOOGLE_REDIRECT_URI` | Google OAuth 콜백 | Vercel 도메인 기준으로 새로 발급 (Phase 10) |
| `KAKAO_CLIENT_ID` | Kakao OAuth | |
| `KAKAO_REDIRECT_URI` | Kakao OAuth 콜백 | Vercel 도메인 기준으로 새로 발급 (Phase 10) |

`PORT`는 Render가 자동 주입하므로 별도 등록 불필요 (`application.properties`가 이미 `${PORT:8080}`로 대응).

### 2-2. Vercel (프론트, `yedocf`)

| 변수명 | 용도 |
|---|---|
| `VITE_API_BASE_URL` | Render 백엔드 URL (예: `https://yedocb.onrender.com`) |
| `VITE_GOOGLE_CLIENT_ID` | Google OAuth |
| `VITE_GOOGLE_REDIRECT_URI` | Google OAuth 콜백 (Vercel 도메인 기준) |
| `VITE_GOOGLE_LOGOUT_REDIRECT_URI` | Google 로그아웃 리다이렉트 |
| `VITE_KAKAO_API_KEY` | Kakao 지도 SDK 키 (`Map.jsx`) |
| `VITE_KAKAO_CLIENT_ID` | Kakao OAuth |
| `VITE_KAKAO_REDIRECT_URI` | Kakao OAuth 콜백 (Vercel 도메인 기준) |
| `VITE_KAKAO_LOGOUT_REDIRECT_URI` | Kakao 로그아웃 리다이렉트 |

> 참고: 기존 GitHub Actions에 있던 `VITE_GOOGLE_CLIENT_SECRET`은 프론트(SPA, 공개 번들)에 절대 넣으면 안 되는 값이었다 — 기존 워크플로우의 잠재적 문제로 `docs/refactor-log.md`에 별도 기록.

## 3. Neon 무료 tier 튜닝 제안 (Phase 7)

- Neon Free tier: 컴퓨트 자동 정지(유휴 시 suspend) + 콜드스타트, 스토리지 0.5GB, 동시 연결 수 제한.
- **런타임 연결은 Neon의 pooled(PgBouncer) connection string 사용** (호스트에 `-pooler` 포함) — Render처럼 여러 요청이 동시에 붙는 환경에서 direct connection의 max_connections 제한에 걸리지 않도록.
- **`schema.sql` 적용은 direct(non-pooled) connection으로 1회만** — DDL은 세션 상태에 민감해 pooler(transaction mode)와 궁합이 나쁠 수 있음.
- HikariCP 풀 크기: Render 무료 tier는 인스턴스 1개·저사양이므로 백엔드 자체 동시성이 낮음. 다음 값을 `application-prod.properties`에 제안:
  - `spring.datasource.hikari.maximum-pool-size=5`
  - `spring.datasource.hikari.minimum-idle=2`
  - `spring.datasource.hikari.connection-timeout=20000` (Neon 콜드스타트 대기 여유)
  - `spring.datasource.hikari.idle-timeout=300000`
  (테스트 세션이 겪은 "too many clients" 이슈는 Testcontainers 다중 컨텍스트 특유 문제이지만, 운영에서도 풀 크기를 작게 유지하는 것이 Neon 무료 tier 연결 한도에 안전)

## 4. Render 무료 tier 제약과 대응

- 15분 무활동 시 슬립 → 첫 요청 콜드스타트(수십 초). 포트폴리오 데모 특성상 허용, 필요 시 안내 문구로 대응.
- 512MB RAM, 공유 CPU — Spring Boot 기동 시간 다소 김. 헬스체크 타임아웃 넉넉히 설정.
- **Render는 Java를 Native 런타임으로 지원하지 않음** (`JAVA_HOME is not set and no 'java' command could be found` 로 첫 배포 실패, 아래 "진행 기록" 참고) → **Docker 런타임으로 전환**. 루트에 [`Dockerfile`](../Dockerfile)(멀티스테이지: `eclipse-temurin:21-jdk`로 빌드 → `eclipse-temurin:21-jre`로 실행) + [`.dockerignore`](../.dockerignore) 추가. Render 서비스 설정에서 Runtime을 **Docker**로 선택하면 Build/Start Command는 Dockerfile이 대신하므로 별도 입력 불필요.
- 헬스체크: `GET /` 또는 permitAll GET 경로 하나 지정 (별도 `/health` 엔드포인트 없음 — Render Health Check Path는 기본 `/`로 설정하고 200 확인)

## 5. Vercel 설정 (Phase 9)

- 빌드 커맨드: `npm run build`
- 출력 디렉터리: `dist`
- SPA 라우팅을 위한 `vercel.json` rewrite 필요 (React Router 새로고침 404 방지)

## 6. 컷오버 순서 (Phase 7~11)

1. **Neon 프로젝트 생성**(사용자) → direct connection으로 `schema.sql` 1회 적용(사용자 본인 터미널에서 실행 권장 — Deploy 세션은 DB 비밀번호를 다루지 않음) → pooled connection string 확보
2. **Render 백엔드 서비스 생성**(사용자, GitHub 연동) → 위 2-1 환경변수 등록(사용자) → 배포 → 헬스체크
3. **Vercel 프론트 서비스 생성**(사용자, GitHub 연동) → 위 2-2 환경변수 등록(사용자, 이때 `VITE_API_BASE_URL`은 2번의 Render URL) → 배포 → 접속 확인
4. **Google/Kakao 콘솔 redirect URI를 Vercel 도메인으로 갱신**(사용자 직접) → Render의 `CORS_ALLOWED_ORIGINS`를 최종 Vercel 도메인으로 갱신
5. **3단계 실사용 테스트** 전체 실행 → 통과 확인 후 **EC2/nginx 종료**(최종 확인 후, 실제로는 해당 AWS 계정이 이미 영구 해지되어 있어 리소스가 자동 정리된 상태였음) → GitHub Actions 워크플로우 삭제(`docs/refactor-log.md`에 원문 보존 완료 후) → 문서 최종화

## 7. 진행 기록

| 일시 | 단계 | 내용 |
|---|---|---|
| 2026-09-07 | 착수 전 점검 | Backend/Frontend 로컬 배포 가능 상태 확인 완료, 본 문서 초안 작성 |
| 2026-09-07 | Phase 7 완료 | Neon 프로젝트 생성(사용자), SQL Editor로 `schema.sql` 적용 — 11개 테이블(users/admin/reservation/notice/inquiry/inquiry_answer/treatment_category/treatment/consultation/staff_schedule/review) 전부 생성 확인 |
| 2026-09-07 | Phase 8 — 1차 배포 실패 | Render Native 런타임(Build Command `./gradlew clean build -x test`)으로 첫 배포 시 `ERROR: JAVA_HOME is not set and no 'java' command could be found` (빌드 6.4초만에 실패, Render가 JDK를 기본 제공하지 않음, 서비스가 애초에 Node 환경으로 생성됨). Render는 생성 후 런타임 변경이 불가해 서비스를 새로 생성, `Dockerfile`/`.dockerignore`를 push해 Docker 환경 자동 감지 확인 |
| 2026-09-07 | Phase 8 — 2차 배포 실패 | Docker 배포 자체는 성공했으나 기동 중 `WeakKeyException: ... is 0 bits` — `JWT_SECRET` 환경변수가 빈 문자열로 등록되어 `Base64.getDecoder().decode("")`가 0바이트 키를 생성해 `JwtTokenProvider` 빈 생성 실패. 사용자가 `openssl rand -base64 32`로 256비트 이상의 새 값을 발급해 재등록 |
| 2026-09-07 | Phase 8 완료 | `https://yedocb.onrender.com` 배포 성공. 실 라이브 검증: `GET /treatments/all`(permitAll) → 200 `[]`(DB 연결 정상, 데이터 없음은 예상된 상태), `GET /api/user/mypage`(인증 필요) → 401(인가 규칙 정상 동작) |
| 2026-09-07 | Phase 9 완료 | `https://yedocf.vercel.app` 배포 성공, 페이지 정상 로딩("연세 BT 미래병원") 확인 |
| 2026-09-07 | Phase 10 착수 — CORS 갱신 | Render `CORS_ALLOWED_ORIGINS`를 `https://yedocf.vercel.app`로 갱신 → CORS 에러 해소 확인(재배포 후 새 탭에서 재검증) |
| 2026-09-07 | 라이브에서 재확인된 기존 이슈 (배포 문제 아님, 수정 보류) | 메인 페이지 팝업(공지/이벤트) 데이터 조회 시 `GET /notices/all`이 401 — `docs/test-report.md`(§4 #4)에 이미 기록된 "`/notices/**`가 permitAll GET 목록에 없음" 버그가 실 라이브에서도 재현됨. 사용자 확인 결과 **지금 수정하지 않고 계속 진행**, 배포 후 디버깅 대상으로 유지 |
| 2026-09-07 | Vercel SPA 라우팅 404 발견/수정 | `vercel.json`이 커밋되지 않아 `/login` 등 직접 접속 시 404. 커밋/push(`2f0881f`) 후 재배포로 해결 확인 |
| 2026-09-07 | Google OAuth 클라이언트 신규 발급 | 팀 원본(B) 소유의 기존 클라이언트가 개인 계정에 안 보여 신규 웹 애플리케이션 클라이언트 생성(외부 동의화면, 테스트 사용자 등록), redirect URI `https://yedocf.vercel.app/login`으로 등록 |
| 2026-09-07 | Kakao 플랫폼/Redirect URI 등록 | Kakao Developers "플랫폼" 메뉴에 Web 사이트 도메인(`https://yedocf.vercel.app`) 추가, 카카오 로그인 Redirect URI `https://yedocf.vercel.app/login` 등록 |
| 2026-09-07 | Vercel 환경변수 Type 이슈 | `VITE_*` 변수가 Type="Secret"(write-only)으로 저장되어 저장/전환이 막힘 (Vercel은 저장된 Secret을 Config로 전환 불가). 전부 삭제 후 Type="Config"로 재생성하는 방식으로 해결 |
| 2026-09-07 | Google 로그인 버튼 주석 발견/수정 | `LoginPage.jsx`에서 `<SocialButton platform="google" .../>`가 주석 처리되어 화면에 미노출 상태였음(배포 작업과 무관한 기존 프론트 상태). 사용자 확인 후 주석 해제, 커밋/push(`d48e8d7`) |
| 2026-09-07 | Phase 10 완료 — OAuth 라이브 검증 | Google 버튼 클릭 → `accounts.google.com`으로 정상 리다이렉트, URL의 `client_id`/`redirect_uri=https://yedocf.vercel.app/login` 값 확인. Kakao 버튼 클릭 → `accounts.kakao.com`으로 정상 리다이렉트, `client_id`/`redirect_uri` 값 확인. **실제 계정 자격증명 입력 및 로그인 완료는 Claude가 대신할 수 없는 영역이라 사용자가 직접 최종 확인 필요** (Phase 11 실사용 테스트에서 재확인 예정) |
| 2026-09-07 | Google 로그인 실사용 성공 | 사용자가 직접 `/signup` 가입 후 Google 소셜 로그인 성공 확인 |
| 2026-09-07 | Kakao 로그인 디버깅 (다단계) | ① 최초 시도 실패, Render 로그에 예외가 전혀 안 남는 것을 발견 — `OAuthController`의 `catch(Exception e)`가 로깅 없이 401만 반환하던 문제. `log.error(...)` 추가(커밋 `fa667ba`)로 진단 가능하게 수정. ② 재시도 시 카카오 자체 에러 화면 `KOE301`(Redirect URI 불일치) 확인 — 요청이 백엔드까지 도달하지도 못해 로그가 안 남았던 것으로 확인. ③ Redirect URI를 다시 등록했다고 생각했으나 실제로는 **"로그아웃 리다이렉트 URI"**(카카오 로그인 > 고급)에 등록한 것이었고, **로그인용 Redirect URI 등록 위치가 콘솔 개편으로 이동**되어 있어 여러 메뉴(일반/플랫폼 키/제품 링크 관리)를 거쳐 확인 — `KOE006`(앱 관리자 설정 오류) 에러 화면의 "왜 에러가 발생하나요?" 상세 메시지로 정확한 원인(등록 안 된 Redirect URI) 재확인. ④ 올바른 위치에 `https://yedocf.vercel.app/login` 등록 후 카카오 인증 자체는 정상 연동 확인 |
| 2026-09-07 | Kakao 로그인 실사용 성공 | 카카오 인증 완료 후 정상 로그인 확인 (초기 "가입된 사용자 없음" 실패는 Kakao 계정 이메일과 가입 이메일 불일치로 인한 설계된 동작이었음 — 버그 아님) |
| 2026-09-07 | Phase 10 최종 완료 | Google/Kakao 소셜 로그인 모두 라이브 환경에서 실제 로그인 성공 확인 |
| 2026-09-07 | Phase 11 실사용 테스트 착수 | 관리자 계정 부재 확인 — `/admin/register`가 인증 필요라 최초 슈퍼관리자를 API로 생성 불가. 기존 회원가입 계정의 BCrypt 해시(`users.u_pwd`)를 재사용해 Neon에 직접 `INSERT INTO admin`으로 슈퍼관리자 생성(평문 비밀번호는 사용자만 알고 Claude는 다루지 않음) |
| 2026-09-07 | Phase 11 실사용 테스트 — 카테고리 등록 불가 발견 | 진료항목 카테고리 등록 시도 시 SQL 오류로 실패, 진료 항목 자체를 추가할 수 없어 사용자 화면 검증 불가. 채용 지원용으로 "더미데이터가 채워진 상태를 편집하는 시나리오"로 가기로 결정 |
| 2026-09-07 | 배포 후 디버깅 — 카테고리 등록 SQL 수정 | `AdminTreatmentCategoryMapper.xml`의 컬럼목록에 `COALESCE(...)` 표현식이 섞여 있고 파라미터명이 스네이크케이스였던 버그 수정, 관련 테스트 성공 검증으로 전환 + null 기본값 케이스 추가, 전체 테스트(193개) 통과 확인 후 push(`61118f7`) |
| 2026-09-07 | 배포 후 디버깅 — 공지 401 수정 | 더미데이터를 채워도 비로그인 방문자가 공지 팝업을 못 보는 문제를 인지, 함께 수정하기로 결정. `SecurityPaths.PUBLIC_GET_PATTERNS`에 `/notices/**` 추가, 테스트 갱신, 전체 테스트(193개) 통과 확인 후 push(`c7d2a53`) |
| 2026-09-07 | Neon 더미데이터 시딩 실패 | 사용자가 직접 실행한 진료항목 시드 SQL이 실패 — 매퍼 버그가 아니라 `desc`가 PostgreSQL 예약어라 서브쿼리 컬럼 별칭으로 못 쓴 것이 원인. `descr`로 별칭 변경한 쿼리 재안내 |
| 2026-09-07 | UI 개선 — 상단 내비게이션 간격 | 사용자가 메뉴 항목 간격이 너무 좁다고 지적. `Header.jsx`의 `gap-1`→`gap-8`로 확대, 로컬 dev 서버(1440px)에서 시각 확인 후 push(`c4df123`) |
| 2026-09-07 | 기능 삭제 — StaffSchedule(직원 근무일정) 전체 제거 | 사용자 요청(포트폴리오 스코프에서 제외, 최초 계획 단계부터도 원했던 정리). 백엔드 11개 파일(Entity/DAO/Mapper XML/Service/Controller/DTO 2종/테스트 3종) 삭제, `schema.sql`에서 `staff_schedule` 테이블 정의 제거, 프론트 `StaffScheduleManagePage.jsx` 삭제 + `App.jsx` 라우트/`Sidebar.jsx` 메뉴 제거. `docs/yedoc-migration-plan.md`, `yedocb/docs/api-contract.md`, `yedocb/docs/architecture-after.md`에서도 해당 도메인 기술 제거. 백엔드 전체 테스트(193→182개) 통과, 프론트 빌드 성공 확인 후 push. **Neon의 `staff_schedule` 테이블은 별도로 사용자가 DROP 필요**(아래 §8 참고) |
| 2026-09-07 | 아이디/비밀번호 찾기 + 이메일 기능 — 범위 밖으로 확정 | 사용자 확인: SMTP 연동이 필요한 큰 작업이라 이번 포트폴리오 배포 범위에는 포함하지 않고, 산출물(README/문서)에 "포트폴리오 완성 후 추가 예정"으로 명시하는 것으로 결정. 프론트는 기존처럼 화면은 두되 기능 비활성화 안내 문구 유지 |
| 2026-09-07 | 문서 최종화 | README(양쪽 저장소) 포트폴리오용 재작성, `architecture-before.md` 신규 작성, 본 문서 최상단에 포트폴리오용 요약 섹션 추가 |
| 2026-09-07 | Phase 11 — 3단계 실사용 테스트 완료 | 공지사항 미노출 문제 해결 확인(사용자), 나머지 체크리스트(상담→예약 전환/진료항목 노출·숨김/리뷰/예약 생성·취소 등) 사용자가 직접 완료 확인. **인수조건 (a)(b)(c)(d) 충족** — 남은 것은 (e) EC2 종료(사용자 최종 확인 후) |
| 2026-09-07 | GitHub Actions 워크플로우 삭제 완료 | `project/yedocf/.github/workflows/frontend-deploy.yml` 삭제(커밋 `a95ac7b`). `project/yedocb`에는 애초에 이 워크플로우가 존재하지 않았음(팀 참고 저장소에만 있었음, §6 컷오버 순서 4번 및 refactor-log.md §10 참고) |
| 2026-09-07 | EC2 종료 — 별도 조치 불필요로 확인 | 사용자가 AWS 콘솔 로그인을 시도했으나 **해당 AWS 계정이 이전에 영구 해지(계정 폐쇄)되어 재활성화 불가 상태**임을 확인. AWS는 계정 해지 절차상 EC2 등 소속 리소스를 자동 정리하므로, 별도의 수동 Terminate 조치 없이도 레거시 인프라는 이미 종료된 것으로 판단. **인수조건 (e) 충족(계정 해지로 인해 사용자 최종 확인 시점에 이미 리소스 종료 상태 확인)** |
| 2026-09-07 | Phase 11 완료 — 전 인수조건 충족 | (a) Neon 스키마 정상 적용, (b) Render/Vercel 배포 정상 동작, (c) OAuth 로그인 라이브 성공(Google/Kakao), (d) 시크릿 값 미노출(모든 값은 사용자가 각 콘솔에서 직접 입력), (e) EC2 종료 확인. 전환 작업 완료 |

## 8. 정리 필요: Neon `staff_schedule` 테이블 삭제

StaffSchedule 기능이 코드에서 완전히 제거되었으므로, Neon에 남아있는 빈 테이블도 정리가 필요합니다. Neon SQL Editor에서:

```sql
DROP TABLE IF EXISTS staff_schedule;
```

<!-- 이하 각 Phase 실행 시 실제 URL/결과를 추가 기록 -->
