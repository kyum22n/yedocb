# API Contract — Phase 1 (초안)

대상 도메인: User, AdminUser, Reservation(+Admin), Admin, NoticeEvent(+Admin), Inquiry(+Admin)

공통 사항:
- 모든 성공 응답은 `ResponseEntity<ExactDtoType>`을 그대로 반환한다 (공용 `ApiResponse<T>` 래퍼 없음).
- 예외 발생 시 공통 `ErrorResponse` 형태로 반환된다: `{ status: number, message: string, timestamp: string(ISO-8601), fieldErrors?: [{field, reason}] }`.
- 날짜/시간 타입은 Java 기준 `LocalDate`(`YYYY-MM-DD`), `LocalTime`(`HH:mm:ss`), `LocalDateTime`(`YYYY-MM-DDTHH:mm:ss`)으로 직렬화된다.
- `uPwd`(User 비밀번호 해시)는 어떤 응답 DTO에도 포함되지 않는다.

---

## 1. User (`/api/user`)

### POST `/api/user/register` — 회원가입
- Request Body: `UserCreateRequestDto`
  - `uId: string` (필수, 4~20자)
  - `uPwd: string` (필수, 8~20자, 영문+숫자+특수문자 포함)
  - `uName: string` (필수)
  - `uEmail: string` (필수, 이메일 형식)
  - `uPhone: string`
  - `uBirth: string (LocalDate)`
  - `uGender: string`
- Response: `Integer` (insert된 row 수, 보통 1)
- 예외: 아이디/이메일 중복 시 409 (`DuplicateResourceException`)

### GET `/api/user/mypage?uId={uId}` — 마이페이지 조회
- Query: `uId: string`
  - TODO: JWT 연동 후 인증 주체에서 획득하도록 변경 예정 (현재는 쿼리 파라미터)
- Response: `UserMypageResponseDto`
  - `uId, uName, uEmail, uPhone, uBirth(LocalDate), uGender`
- 예외: 존재하지 않으면 404 (`ResourceNotFoundException`)

### PUT `/api/user/mypage/update` — 마이페이지 수정
- Request Body: `UserMypageUpdateRequestDto`
  - `uId: string` (필수)
  - `uName, uPhone, uBirth(LocalDate), uGender`
- Response: `Integer` (update된 row 수)
- 예외: 존재하지 않으면 404

### DELETE `/api/user/withdraw?uId={uId}` — 회원 탈퇴
- Query: `uId: string`
- Response: `Integer` (delete된 row 수)
- 예외: 존재하지 않으면 404

---

## 2. AdminUser (`/api/admin/user`)

### POST `/api/admin/user/register` — 관리자 화면에서 회원 등록
- Request Body: `UserCreateRequestDto` (User 등록과 동일)
- Response: `Integer`

### GET `/api/admin/user/list` — 회원 목록 조회
- Response: `AdminUserListResponseDto[]`
  - `uId, uName, uEmail, uPhone, createdAt(LocalDateTime)`
  - (비밀번호 해시 미포함 — 이전 버그 수정됨)

### GET `/api/admin/user/detail?uId={uId}` — 회원 상세 조회
- Response: `AdminUserDetailResponseDto`
  - `uId, uName, uEmail, uPhone, uBirth(LocalDate), uGender, createdAt, updatedAt`
- 예외: 존재하지 않으면 404

### DELETE `/api/admin/user/delete?uId={uId}` — 회원 삭제
- Response: `Integer`
- 예외: 존재하지 않으면 404

---

## 3. Reservation (`/reservations`, `/admin/reservations`)

### POST `/reservations/register` — 예약 등록
- Request Body: `ReservationCreateRequestDto`
  - `uId: string`, `treatmentId: number`, `reservationDate(LocalDate)`, `reservationTime(LocalTime)`, `memberMemo: string`
- Response: `Integer`

### GET `/reservations/member?uId={uId}` — 회원별 예약 목록
- Response: `ReservationResponseDto[]`
  - `reservationId, uId, treatmentId, reservationDate, reservationTime, reservationStatus, memberMemo, createdAt, updatedAt`

### GET `/reservations/{reservationId}?uId={uId}` — 예약 상세
- Response: `ReservationResponseDto`
- 예외: 없으면 404 (현재는 `IllegalArgumentException`으로 남아있음 — 500으로 처리됨, Phase 2에서 개선 검토)

### PUT `/reservations/update` — 예약 수정
- Request Body: `ReservationUpdateRequestDto` (`reservationId, uId, treatmentId, reservationDate, reservationTime, memberMemo`)
- Response: `Integer`

### PUT `/reservations/cancel` — 예약 취소
- Request Body: `ReservationCancelRequestDto` (`reservationId, uId`)
- Response: `Integer`

### Admin 전용

| Method | Path | Request | Response |
|---|---|---|---|
| POST | `/admin/reservations/register` | `AdminReservationCreateRequestDto` (`uId, treatmentId, adminId, reservationDate, reservationTime, reservationStatus, memberMemo, adminMemo, pmsSyncStatus`) | `Integer` |
| GET | `/admin/reservations/all` | - | `AdminReservationResponseDto[]` |
| GET | `/admin/reservations/member?uId=` | `uId: string` | `AdminReservationResponseDto[]` |
| GET | `/admin/reservations/admin?adminId=` | `adminId: number` | `AdminReservationResponseDto[]` |
| GET | `/admin/reservations/status?reservationStatus=` | `reservationStatus: string` (PENDING/CONFIRMED/COMPLETED/CANCELED/NO_SHOW) | `AdminReservationResponseDto[]` |
| GET | `/admin/reservations/pms-status?pmsSyncStatus=` | `pmsSyncStatus: string` (PENDING/SUCCESS/FAILED) | `AdminReservationResponseDto[]` |
| GET | `/admin/reservations/{reservationId}` | - | `AdminReservationResponseDto` |
| PUT | `/admin/reservations/update` | `AdminReservationUpdateRequestDto` | `Integer` |
| PUT | `/admin/reservations/status/update` | `AdminReservationStatusUpdateRequestDto` (`reservationId, adminId, reservationStatus, adminMemo`) | `Integer` |
| PUT | `/admin/reservations/pms-status/update` | `AdminPmsSyncStatusUpdateRequestDto` (`reservationId, pmsSyncStatus`) | `Integer` |
| DELETE | `/admin/reservations/delete/{reservationId}` | - | `Integer` |

`AdminReservationResponseDto` 필드: `reservationId, uId, treatmentId, adminId, reservationDate, reservationTime, reservationStatus, memberMemo, adminMemo, pmsSyncStatus, createdAt, updatedAt`

---

## 4. Admin (`/admin`)

주의: 이 도메인은 예외 처리(Task 4)만 적용했고, DTO 사용 방식(예: 등록/목록이 전용 DTO가 아닌 엔티티를 그대로 주고받는 부분)은 이번 Phase 1에서 변경하지 않았다. 아래는 **현재 실제 동작**을 그대로 기술한다.

| Method | Path | Request | Response |
|---|---|---|---|
| GET | `/admin/list` | - | `Admin[]` (엔티티 그대로 반환 — `adminPassword` 해시 포함 필드 존재. Phase 2에서 `AdminListResponseDto`로 교체 검토 필요) |
| GET | `/admin/detail?adminId=` | `adminId: number` | `AdminDetailResponseDto` (`adminId, adminLoginId, adminName, adminEmail, adminPhone, adminRole, createdBy, createdAt(LocalDateTime), updatedAt(LocalDateTime)`) |
| POST | `/admin/register` | `Admin` (엔티티 그대로 요청 바디로 받음 — `adminLoginId, adminPassword, adminName, adminEmail, adminPhone, adminRole`) | `Integer` |
| PUT | `/admin/update` | `AdminUpdateRequestDto` (`adminId, adminName, adminEmail, adminPhone`) | `Integer` |
| DELETE | `/admin/delete/{adminId}` | - | `Integer` |

예외: 존재하지 않는 관리자 조회/수정/삭제 시 404, 로그인 ID 중복 등록 시 409.

---

## 5. NoticeEvent / Notice (`/notices`, `/admin/notices`)

### 사용자 (`/notices`)

| Method | Path | Request | Response |
|---|---|---|---|
| GET | `/notices/all` | - | `NoticeResponseDto[]` (노출(`isVisible=true`) 공지/이벤트만) |
| GET | `/notices/type?noticeType=` | `noticeType: string` (`NOTICE`/`EVENT`) | `NoticeResponseDto[]` |
| GET | `/notices/{noticeId}` | - | `NoticeResponseDto` |

`NoticeResponseDto` 필드: `noticeId, title, content, noticeType, imageUrl, startAt(LocalDateTime), endAt(LocalDateTime), createdAt`

### 관리자 (`/admin/notices`)

| Method | Path | Request | Response |
|---|---|---|---|
| POST | `/admin/notices/register` | `AdminNoticeCreateRequestDto` (`title, content, noticeType, imageUrl, isVisible, startAt, endAt, createdBy`) | `Integer` |
| GET | `/admin/notices/all` | - | `AdminNoticeResponseDto[]` |
| GET | `/admin/notices/type?noticeType=` | `noticeType: string` | `AdminNoticeResponseDto[]` |
| GET | `/admin/notices/{noticeId}` | - | `AdminNoticeResponseDto` |
| PUT | `/admin/notices/update` | `AdminNoticeUpdateRequestDto` (`noticeId, title, content, noticeType, imageUrl, isVisible, startAt, endAt`) | `Integer` |
| DELETE | `/admin/notices/delete/{noticeId}` | - | `Integer` |

`AdminNoticeResponseDto` 필드: 위 + `createdBy, createdAt, updatedAt`

예외: 존재하지 않으면 404.

---

## 6. Inquiry (`/inquiries`, `/admin/inquiries`)

### 사용자 (`/inquiries`)

| Method | Path | Request | Response |
|---|---|---|---|
| POST | `/inquiries/register` | `InquiryCreateRequestDto` (`uId: string, inquiryType: string(RESERVATION/TREATMENT/PAYMENT/ETC), title, content`) | `Integer` |
| GET | `/inquiries/member?uId=` | `uId: string` | `InquiryResponseDto[]` |
| GET | `/inquiries/{inquiryId}?uId=` | `uId: string` | `InquiryResponseDto` |
| PUT | `/inquiries/update` | `InquiryUpdateRequestDto` (`inquiryId, uId, inquiryType, title, content`) | `Integer` |
| DELETE | `/inquiries/delete` | `InquiryDeleteRequestDto` (`inquiryId, uId`) | `Integer` |

`InquiryResponseDto` 필드: `inquiryId, uId, inquiryType, title, content, inquiryStatus, answer(InquiryAnswerResponseDto \| null), createdAt, updatedAt`
`InquiryAnswerResponseDto` 필드: `answerId, inquiryId, adminId, answerContent, createdAt, updatedAt`

### 관리자 (`/admin/inquiries`)

| Method | Path | Request | Response |
|---|---|---|---|
| GET | `/admin/inquiries/all` | - | `AdminInquiryResponseDto[]` |
| GET | `/admin/inquiries/member?uId=` | `uId: string` | `AdminInquiryResponseDto[]` |
| GET | `/admin/inquiries/type?inquiryType=` | `inquiryType: string` | `AdminInquiryResponseDto[]` |
| GET | `/admin/inquiries/status?inquiryStatus=` | `inquiryStatus: string` (`WAITING`/`ANSWERED`) | `AdminInquiryResponseDto[]` |
| GET | `/admin/inquiries/{inquiryId}` | - | `AdminInquiryResponseDto` |
| PUT | `/admin/inquiries/status/update` | `AdminInquiryStatusUpdateRequestDto` (`inquiryId, inquiryStatus`) | `Integer` |
| POST | `/admin/inquiries/answers/register` | `AdminInquiryAnswerCreateRequestDto` (`inquiryId, adminId, answerContent`) | `Integer` (등록 성공 시 문의 상태가 자동으로 `ANSWERED`로 변경됨) |
| PUT | `/admin/inquiries/answers/update` | `AdminInquiryAnswerUpdateRequestDto` (`answerId, answerContent`) | `Integer` |
| DELETE | `/admin/inquiries/answers/delete/{answerId}` | - | `Integer` (삭제 성공 시 문의 상태가 자동으로 `WAITING`으로 변경됨) |
| DELETE | `/admin/inquiries/delete/{inquiryId}` | - | `Integer` |

`AdminInquiryResponseDto` 필드: `InquiryResponseDto`와 동일(`uId` 포함, 문의자 본인 확인 없이 전체 조회 가능).

예외: 존재하지 않는 문의/답변 조회·수정·삭제 시 404.

---

## 7. 인증 (JWT / OAuth)

모든 인증 필요 엔드포인트는 `Authorization: Bearer {accessToken}` 헤더가 필요하다. 액세스 토큰 만료(기본 1시간) 시 `/api/user/refresh`로 재발급받는다.

### POST `/api/user/login` — 로그인 (permitAll)
- Request Body: `UserLoginRequestDto { uId: string, uPwd: string }`
- Response: `TokenResponseDto { accessToken: string, refreshToken: string, userId: string }`
- 예외: 아이디/비밀번호 불일치 시 401 (`InvalidCredentialsException`)

### POST `/api/user/refresh?refreshToken={refreshToken}` — 액세스 토큰 재발급 (permitAll)
- Response: `TokenResponseDto { accessToken, refreshToken(입력값 그대로), userId }`
- 예외: 리프레시 토큰이 유효하지 않거나 서버가 기억하는 값과 다르면 401
- **주의**: 리프레시 토큰은 서버 인메모리(`ConcurrentHashMap`)에 저장된다 — 서버 프로세스 재시작(Render 재배포/idle 재시작 등) 시 전부 초기화되어 재로그인이 필요하다. `docs/refactor-log.md` §7 참고.

### POST `/api/admin/login` — 관리자 로그인 (permitAll)
- Request Body: `AdminLoginRequestDto { adminLoginId: string, adminPassword: string }`
- Response: `TokenResponseDto { accessToken, refreshToken: null, userId: adminLoginId }` (관리자 로그인은 리프레시 토큰을 발급하지 않음 — B 원본 동작 유지)
- 예외: 401 (`InvalidCredentialsException`)

### POST `/api/oauth2/google?code={code}` / POST `/api/oauth2/kakao?code={code}` — 소셜 로그인 (permitAll)
- Response: `TokenResponseDto { accessToken, refreshToken: null, userId }` (OAuth 로그인도 리프레시 토큰 미발급)
- 실패 케이스:
  - 이메일 미인증(Google `email_verified=false` / Kakao `is_email_verified=false`) → 401 + 안내 메시지
  - 해당 이메일로 가입된 회원이 없음 → 401 + "회원가입이 필요합니다" 메시지
- 프론트는 Google/Kakao 인가 코드(`code`)를 먼저 받아온 뒤 이 엔드포인트에 전달해야 한다 (OAuth 콘솔 설정은 Deploy 단계에서 최종화 예정).

---

## 알려진 이슈 / 프론트엔드 유의사항

1. **User 도메인만 `/api/...` 접두사**를 쓰고, 나머지 Phase 1 도메인(Reservation/Notice/Inquiry/Admin)은 기존 경로(`/reservations`, `/notices`, `/inquiries`, `/admin/...`)를 그대로 유지한다. Phase 2에서 전체 경로 컨벤션 통일 여부를 결정할 예정.
2. Reservation/Inquiry의 `memberId`(number)는 전부 `uId`(string)로 변경되었다 — 기존에 프론트가 숫자 회원 ID를 넘기고 있었다면 로그인 ID(문자열)로 바꿔야 한다.
3. `/admin/list`(관리자 목록)와 `/admin/register`(관리자 등록)는 아직 전용 DTO가 아닌 `Admin` 엔티티를 그대로 주고받는다 — `adminPassword` 해시가 목록 조회 응답에 포함되어 있으니 프론트에서 이 값을 노출하지 않도록 주의. Phase 2에서 개선 예정.
4. 대부분의 `IllegalArgumentException` 기반 "필수값 누락"/"올바르지 않은 값" 검증 오류는 현재 500(Internal Server Error)으로 응답된다 (전용 400 예외가 아직 없음). 확정된 예외 매핑은 404/409/403/400(검증코드)/401 뿐이다. Phase 2에서 `@Valid` 전환 또는 전용 400 예외 도입 검토.
5. `/api/user/**`, `/api/admin/**`는 SecurityConfig에 의해 인증(JWT)이 필요하다 (`register`/`login`/`refresh`/`admin/login`/`oauth2/**` 제외). 그 외 Phase 1 도메인 경로(`/reservations`, `/notices`, `/inquiries`, `/admin/...` 등 `/api` 접두사가 없는 경로)는 `anyRequest().authenticated()`에 걸려 **이제 인증이 필요해졌다** — Phase 1 이전에는 인증 자체가 없었으므로, 프론트에서 이 경로들을 호출할 때도 `Authorization` 헤더를 붙여야 한다.
6. Google/Kakao OAuth 클라이언트 ID/Secret은 로컬 `application.properties`에서 빈 값(`${GOOGLE_CLIENT_ID:}` 등)으로 기본 설정되어 있다 — 로컬에서 소셜 로그인을 테스트하려면 환경변수로 실제 값을 주입해야 한다.
