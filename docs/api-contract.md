# API Contract (Phase 1~2 최종화)

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
- 예외: 없으면 400 (`IllegalArgumentException` — GlobalExceptionHandler가 400으로 매핑, 아래 §7 참고)

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

## 4. Admin (`/admin`, ADMIN/SUPERADMIN 권한 필요 — `/admin/login` 제외)

| Method | Path | Request | Response |
|---|---|---|---|
| GET | `/admin/list` | - | `AdminListResponseDto[]` (`adminId, adminLoginId, adminName, adminEmail, adminPhone, adminRole` — `adminPassword` 미포함) |
| GET | `/admin/detail?adminId=` | `adminId: number` | `AdminDetailResponseDto` (`adminId, adminLoginId, adminName, adminEmail, adminPhone, adminRole, createdBy, createdAt(LocalDateTime), updatedAt(LocalDateTime)`) |
| POST | `/admin/register` | `AdminCreateRequestDto` (`adminLoginId`(필수), `adminPassword`(필수), `adminName`(필수), `adminEmail`(필수, 이메일 형식), `adminPhone`, `adminRole`(미지정 시 `"ADMIN"`으로 기본 설정)) | `Integer` |
| PUT | `/admin/update` | `AdminUpdateRequestDto` (`adminId`(필수), `adminName`(필수), `adminEmail`(필수, 이메일 형식), `adminPhone`) | `Integer` |
| DELETE | `/admin/delete/{adminId}` | - | `Integer` |

예외: 존재하지 않는 관리자 조회/수정/삭제 시 404, 로그인 ID 중복 등록 시 409, 필수값 누락/형식 오류 시 400.

**참고**: `/admin/register`도 이제 `/admin/**`로 보호되므로 ADMIN/SUPERADMIN 토큰 없이는 신규 관리자를 등록할 수 없다 — 최초 SUPERADMIN 계정은 DB에 직접 시드하거나 별도의 부트스트랩 절차가 필요하다(현재 코드에는 부트스트랩 메커니즘이 없음 — Deploy 단계에서 결정 필요).

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
- Response: `AdminTokenResponseDto { accessToken: string, adminId: number, adminLoginId: string, adminRole: string("ADMIN"|"SUPERADMIN") }` (리프레시 토큰 미발급 — B 원본 동작 유지. `TokenResponseDto`가 아니라 전용 DTO이며, 하위 관리자 API가 요청 바디에 필요로 하는 숫자 `adminId`와 프론트 메뉴 분기용 `adminRole`을 함께 내려준다)
- 예외: 401 (`InvalidCredentialsException`)
- **프론트 참고**: `AdminReservation*RequestDto.adminId`, `AdminInquiryAnswerCreateRequestDto.adminId`, `AdminConsultation*RequestDto.adminId` 등 관리자 하위 API가 요청 바디에 받는 숫자 `adminId`는 로그인 응답의 이 `adminId` 값을 그대로 사용하면 된다 — JWT에서 서버가 자동으로 채워주지 않으므로 프론트가 값을 담아 보내야 한다.

### POST `/api/oauth2/google?code={code}` / POST `/api/oauth2/kakao?code={code}` — 소셜 로그인 (permitAll)
- Response: `TokenResponseDto { accessToken, refreshToken: null, userId }` (OAuth 로그인도 리프레시 토큰 미발급)
- 실패 케이스:
  - 이메일 미인증(Google `email_verified=false` / Kakao `is_email_verified=false`) → 401 + 안내 메시지
  - 해당 이메일로 가입된 회원이 없음 → 401 + "회원가입이 필요합니다" 메시지
- 프론트는 Google/Kakao 인가 코드(`code`)를 먼저 받아온 뒤 이 엔드포인트에 전달해야 한다 (OAuth 콘솔 설정은 Deploy 단계에서 최종화 예정).

---

## 알려진 이슈 / 프론트엔드 유의사항

1. **User 도메인만 `/api/...` 접두사**를 쓰고, 나머지 Phase 1 도메인(Reservation/Notice/Inquiry/Admin)은 기존 경로(`/reservations`, `/notices`, `/inquiries`, `/admin/...`)를 그대로 유지한다. 경로 컨벤션 통일은 하지 않기로 확정.
2. Reservation/Inquiry의 `memberId`(number)는 전부 `uId`(string)로 변경되었다 — 기존에 프론트가 숫자 회원 ID를 넘기고 있었다면 로그인 ID(문자열)로 바꿔야 한다.
3. `/api/user/**`, `/api/admin/**`, 접두사 없는 `/admin/**`(예: `/admin/reservations`, `/admin/staff-schedules`)는 모두 SecurityConfig에 의해 ADMIN/SUPERADMIN(또는 USER 이상) 권한이 필요하다 (`register`/`login`/`refresh`/`admin/login`/`oauth2/**` 및 `GET /treatments/**`, `GET /treatment-categories/**`, `GET /reviews/**`는 예외). 리팩토링 이전에는 인증 자체가 없었으므로, 프론트에서 이 경로들을 호출할 때 `Authorization` 헤더를 붙여야 한다.
4. Google/Kakao OAuth 클라이언트 ID/Secret은 로컬 `application.properties`에서 빈 값(`${GOOGLE_CLIENT_ID:}` 등)으로 기본 설정되어 있다 — 로컬에서 소셜 로그인을 테스트하려면 환경변수로 실제 값을 주입해야 한다.

### 해결된 이슈 (참고용 — 이전 버전 문서를 봤다면 최신 상태로 갱신됨)

- ~~`/admin/list`/`/admin/register`가 `Admin` 엔티티를 그대로 주고받아 비밀번호 해시가 노출됨~~ → `AdminListResponseDto`/`AdminCreateRequestDto`로 교체 완료 (§4 참고).
- ~~`IllegalArgumentException` 기반 검증 오류가 500으로 응답됨~~ → `GlobalExceptionHandler`에 `IllegalArgumentException` → 400 매핑 추가로 전 도메인에서 일괄 해결. 이 문서의 개별 엔드포인트 설명에 남아있던 "500(알려진 이슈)" 표기는 전부 400으로 갱신됨.

---

# API Contract — Phase 2 추가분

대상 도메인: Consultation(+Admin), Treatment(+Admin), TreatmentCategory(+Admin), StaffSchedule(Admin 전용), Statistics(Admin 전용), Dashboard(Admin 전용), Review(+Admin, 신규)

공통 사항은 위 Phase 1 섹션과 동일하다(`ResponseEntity<ExactDtoType>` 그대로 반환, 공용 래퍼 없음, `ErrorResponse` 형식 동일).

---

## 8. Consultation (`/consultations`, `/admin/consultations`)

### 사용자 (`/consultations`)

| Method | Path | Request | Response |
|---|---|---|---|
| POST | `/consultations/register` | `ConsultationCreateRequestDto` (`uId: string`(필수), `treatmentId: number`(필수), `consultationMemo, preferredDate(LocalDate), preferredTime(LocalTime)`) | `Integer` |
| GET | `/consultations/member?uId=` | `uId: string` | `ConsultationResponseDto[]` |
| GET | `/consultations/{consultationId}?uId=` | `uId: string` | `ConsultationResponseDto` |
| PUT | `/consultations/update` | `ConsultationUpdateRequestDto` (`consultationId, uId, treatmentId`(모두 필수) `, consultationMemo, preferredDate, preferredTime`) | `Integer` |
| PUT | `/consultations/cancel` | `ConsultationCancelRequestDto` (`consultationId, uId` 모두 필수) | `Integer` |

`ConsultationResponseDto` 필드: `consultationId, uId, reservationId, treatmentId, consultationStatus, consultationMemo, preferredDate, preferredTime, createdAt, updatedAt`

예외: 존재하지 않는 상담 조회/수정/취소 시 404 (`ResourceNotFoundException`), 필수값 누락 시 400 (`IllegalArgumentException` -> GlobalExceptionHandler 매핑).

### 관리자 (`/admin/consultations`)

| Method | Path | Request | Response |
|---|---|---|---|
| POST | `/admin/consultations/register` | `AdminConsultationCreateRequestDto` (`uId`(필수)`, reservationId, treatmentId, adminId, consultationStatus(RECEIVED/SCHEDULED/COMPLETED/CONVERTED/CANCELED), consultationMemo, preferredDate, preferredTime`) | `Integer` |
| GET | `/admin/consultations/all` | - | `AdminConsultationResponseDto[]` |
| GET | `/admin/consultations/member?uId=` | `uId: string` | `AdminConsultationResponseDto[]` |
| GET | `/admin/consultations/admin?adminId=` | `adminId: number` | `AdminConsultationResponseDto[]` |
| GET | `/admin/consultations/status?consultationStatus=` | `consultationStatus: string` | `AdminConsultationResponseDto[]` |
| GET | `/admin/consultations/{consultationId}` | - | `AdminConsultationResponseDto` |
| PUT | `/admin/consultations/update` | `AdminConsultationUpdateRequestDto` (`consultationId`(필수)`, reservationId, treatmentId, adminId, consultationStatus, consultationMemo, preferredDate, preferredTime`) | `Integer` |
| PUT | `/admin/consultations/status/update` | `AdminConsultationStatusUpdateRequestDto` (`consultationId`(필수)`, adminId, consultationStatus`(필수)`, consultationMemo`) | `Integer` |
| PUT | `/admin/consultations/convert` | `AdminConsultationConvertRequestDto` (`consultationId, reservationId`(모두 필수)`, adminId, consultationMemo`) | `Integer` (상담을 예약으로 전환) |
| DELETE | `/admin/consultations/delete/{consultationId}` | - | `Integer` |

`AdminConsultationResponseDto` 필드: `ConsultationResponseDto` + `adminId`.

예외: 존재하지 않는 상담 조회/수정/삭제/전환 시 404. 필수값 누락/잘못된 상담 상태값은 400 (`IllegalArgumentException` -> GlobalExceptionHandler 매핑).

---

## 9. Treatment / TreatmentCategory (`/treatments`, `/admin/treatments`, `/treatment-categories`, `/admin/treatment-categories`)

### 사용자 (`/treatments`, `/treatment-categories`) — **인증 없이 GET 가능** (`SecurityPaths.PUBLIC_GET_PATTERNS`)

| Method | Path | Request | Response |
|---|---|---|---|
| GET | `/treatments/all` | - | `TreatmentResponseDto[]` (노출(`isVisible=true`) 항목만) |
| GET | `/treatments/category?categoryId=` | `categoryId: number` | `TreatmentResponseDto[]` |
| GET | `/treatments/{treatmentId}` | - | `TreatmentResponseDto` |
| GET | `/treatment-categories/all` | - | `CategoryResponseDto[]` (노출 카테고리만) |
| GET | `/treatment-categories/{categoryId}` | - | `CategoryResponseDto` |

`TreatmentResponseDto` 필드: `treatmentId, treatmentName, description, isReservable` (관리 전용 필드인 `categoryId`/`isVisible`은 사용자 응답에 포함되지 않음)
`CategoryResponseDto` 필드: `categoryId, categoryName, isVisible, createdAt, updatedAt`

예외: 존재하지 않는 항목/카테고리 조회 시 404 (`ResourceNotFoundException`).

### 관리자 (`/admin/treatments`, `/admin/treatment-categories`)

| Method | Path | Request | Response |
|---|---|---|---|
| GET | `/admin/treatments/all` | - | `AdminTreatmentResponseDto[]` |
| GET | `/admin/treatments/category?categoryId=` | `categoryId: number` | `AdminTreatmentResponseDto[]` |
| GET | `/admin/treatments/{treatmentId}` | - | `AdminTreatmentResponseDto` |
| POST | `/admin/treatments/register` | `TreatmentCreateRequestDto` (`categoryId`(필수)`, treatmentName`(필수)`, description, isReservable, isVisible`) | `Integer` |
| PUT | `/admin/treatments/update` | `TreatmentUpdateRequestDto` (`treatmentId, categoryId`(필수)`, treatmentName`(필수)`, description, isReservable, isVisible`) | `Integer` |
| DELETE | `/admin/treatments/delete/{treatmentId}` | - | `Integer` |
| GET | `/admin/treatment-categories/all` | - | `CategoryResponseDto[]` (숨김 포함 전체) |
| GET | `/admin/treatment-categories/{categoryId}` | - | `CategoryResponseDto` |
| POST | `/admin/treatment-categories/register` | `CategoryCreateRequestDto` (`categoryName`(필수)`, isVisible`) | `Integer` |
| PUT | `/admin/treatment-categories/update` | `CategoryUpdateRequestDto` (`categoryId`(필수)`, categoryName`(필수)`, isVisible`) | `Integer` |
| DELETE | `/admin/treatment-categories/delete/{categoryId}` | - | `Integer` |

`AdminTreatmentResponseDto` 필드: `TreatmentResponseDto` + `categoryId, isVisible, createdAt, updatedAt`

예외: 존재하지 않는 항목/카테고리 조회·수정·삭제 시 404.

---

## 10. StaffSchedule (`/admin/staff-schedules`, 관리자 전용)

| Method | Path | Request | Response |
|---|---|---|---|
| POST | `/admin/staff-schedules/register` | `AdminStaffScheduleCreateRequestDto` (`adminId`(필수)`, scheduleDate`(필수)`, scheduleType`(필수, `WORK`/`OFF`)`, memo`) | `Integer` |
| GET | `/admin/staff-schedules/all` | - | `AdminStaffScheduleResponseDto[]` |
| GET | `/admin/staff-schedules/admin?adminId=` | `adminId: number` | `AdminStaffScheduleResponseDto[]` |
| GET | `/admin/staff-schedules/date?scheduleDate=` | `scheduleDate: string(LocalDate, ISO)` | `AdminStaffScheduleResponseDto[]` |
| GET | `/admin/staff-schedules/type?scheduleType=` | `scheduleType: string` | `AdminStaffScheduleResponseDto[]` |
| GET | `/admin/staff-schedules/{scheduleId}` | - | `AdminStaffScheduleResponseDto` |
| PUT | `/admin/staff-schedules/update` | `AdminStaffScheduleUpdateRequestDto` (`scheduleId, adminId, scheduleDate, scheduleType`(모두 필수)`, memo`) | `Integer` |
| DELETE | `/admin/staff-schedules/delete/{scheduleId}` | - | `Integer` |

`AdminStaffScheduleResponseDto` 필드: `scheduleId, adminId, scheduleDate, scheduleType, memo, createdAt, updatedAt`

예외:
- 존재하지 않는 일정 조회/수정/삭제 시 404 (`ResourceNotFoundException`, Phase 2에서 교체됨).
- **동일 관리자 + 동일 날짜에 이미 일정이 존재하면 409 (`DuplicateResourceException`)** — Phase 2에서 새로 추가된 검증(기존에는 검증 자체가 없어 중복 등록이 가능했던 버그).
- 필수값 누락/잘못된 일정 유형은 400 (`IllegalArgumentException` -> GlobalExceptionHandler 매핑. `@Valid`로 잡히는 필드 누락은 필드별 에러 메시지 포함, 유형 화이트리스트 검증은 서비스 레벨에서 단일 메시지로 400 응답).

---

## 11. Statistics (`/admin/statistics`, 관리자 전용)

엔티티가 없는 순수 집계 도메인이다. 모든 엔드포인트가 `POST`이며 `@RequestBody`로 조회 조건을 받는다(GET+쿼리스트링이 아님에 유의).

| Method | Path | Request | Response |
|---|---|---|---|
| POST | `/admin/statistics/summary` | `StatisticsSearchRequestDto { startDate?, endDate?: LocalDate }` | `AdminStatisticsResponseDto { reservationStatistics, consultationStatistics, inquiryStatistics, treatmentStatistics[] }` |
| POST | `/admin/statistics/reservations` | 위와 동일 | `ReservationStatisticsResponseDto { totalReservationCount, pendingCount, confirmedCount, completedCount, canceledCount, noShowCount, noShowRate }` |
| POST | `/admin/statistics/consultations` | 위와 동일 | `ConsultationStatisticsResponseDto { totalConsultationCount, receivedCount, scheduledCount, completedCount, convertedCount, canceledCount, conversionRate }` |
| POST | `/admin/statistics/inquiries` | 위와 동일 | `InquiryStatisticsResponseDto { totalInquiryCount, waitingCount, answeredCount }` |
| POST | `/admin/statistics/treatments` | 위와 동일 | `TreatmentStatisticsResponseDto[]` (인기 진료 항목 순위) |

집계 결과가 없거나(DAO가 `null` 반환) 개별 카운트 필드가 `null`이면 서비스 레벨에서 전부 `0`으로 보정한다. `noShowRate`/`conversionRate`는 소수 둘째 자리까지 반올림(`Math.round(x*100.0)/100.0`)하며 총 건수가 0이면 `0.0`을 반환한다(0으로 나누기 방지).

예외: `endDate`가 `startDate`보다 빠르면 400 (`IllegalArgumentException` -> GlobalExceptionHandler 매핑. Statistics는 엔티티 조회가 없어 `ResourceNotFoundException` 리트로핏 대상에서는 제외되었으나, 공통 `IllegalArgumentException` 400 매핑은 동일하게 적용됨).

---

## 12. Dashboard (`/admin/dashboard`, 관리자 전용)

Phase 2에서 신규 문서화. 관리자 대시보드 요약 정보를 제공하는 조회 전용 도메인이며, 상세 스펙은 `AdminDashboardController`/`AdminDashboardService`/`AdminDashboardMapper.xml`을 참고할 것 — 본 문서 갱신 시점 기준 별도 요청 조건 DTO 없이 `GET` 요청만으로 요약 데이터를 반환하는 구조다. Reservation/Inquiry 참조 쿼리는 Phase 1의 `u_id` 컬럼명 변경에 맞춰 이미 정리되어 있다(refactor-log.md §3 참고).

---

## 13. Review (`/reviews`, `/admin/reviews`) — Phase 2 신규 도메인

작성/수정/삭제는 `Authorization: Bearer {accessToken}` 필요. **작성자(`userId`)는 요청 바디/쿼리 파라미터로 받지 않고, 서버가 JWT 인증 주체에서 직접 가져온다** — 프론트는 `userId`를 별도로 보내지 않아도 되며(보내도 무시됨), 로그인한 사용자 본인 명의로만 작성/수정/삭제된다.

### 사용자 (`/reviews`)

| Method | Path | Request | Response |
|---|---|---|---|
| POST | `/reviews/register` | `ReviewCreateRequestDto { treatmentId: number(필수), title: string(필수), content: string(필수), imageUrl?, hashTag? }` | `Integer` (작성자는 JWT의 인증 주체로 자동 설정) |
| GET | `/reviews/all` | - (인증 불필요, permitAll) | `ReviewResponseDto[]` (숨김 처리(`isHidden=true`)된 리뷰 제외) |
| GET | `/reviews/treatment?treatmentId=` | `treatmentId: number` (인증 불필요) | `ReviewResponseDto[]` (숨김 제외) |
| GET | `/reviews/{reviewId}` | - (인증 불필요) | `ReviewResponseDto` (조회 시 `hits` 조회수 1 증가) |
| PUT | `/reviews/update` | `ReviewUpdateRequestDto { reviewId(필수), title(필수), content(필수), imageUrl?, hashTag? }` | `Integer` (작성자 본인만 가능, JWT로 확인) |
| DELETE | `/reviews/delete?reviewId=` | `reviewId: number` | `Integer` (작성자 본인만 가능, JWT로 확인) |

`ReviewResponseDto` 필드: `reviewId, treatmentId, userId, title, content, imageUrl, hashTag, hits, createdAt, updatedAt` (`isHidden`은 포함하지 않음 — 관리자 전용 정보)

예외:
- 존재하지 않는 리뷰 조회/수정/삭제 시 404 (`ResourceNotFoundException`)
- 본인이 작성한 리뷰가 아니면 403 (`UnauthorizedActionException`) — 판단 기준은 JWT 인증 주체이며 요청 바디 값이 아니다.

### 관리자 (`/admin/reviews`) — 모더레이션

| Method | Path | Request | Response |
|---|---|---|---|
| GET | `/admin/reviews/all` | - | `AdminReviewResponseDto[]` (숨김 리뷰 포함 전체) |
| PUT | `/admin/reviews/hide?reviewId=&isHidden=` | `reviewId: number, isHidden?: boolean(기본값 true)` | `Integer` (숨김/노출 전환 — `isHidden=false`로 호출하면 다시 노출) |
| DELETE | `/admin/reviews/delete?reviewId=` | `reviewId: number` | `Integer` (작성자 제한 없이 삭제 가능) |

`AdminReviewResponseDto` 필드: `ReviewResponseDto` + `isHidden`

예외: 존재하지 않는 리뷰 조회/숨김처리/삭제 시 404.

**참고**: `GET /reviews/**`는 로그인 전 리뷰 둘러보기가 자연스러운 유스케이스이지만, 이번 Phase 2에서는 `SecurityPaths`/`SecurityConfig`를 직접 수정하지 않았다(오케스트레이팅 세션의 판단 필요 항목 — refactor-log.md Phase 2 §6 참고). 문서화 시점 기준 `/reviews/**`도 `anyRequest().authenticated()`에 걸려 인증이 필요한 상태다.

---

## 알려진 이슈 / 프론트엔드 유의사항 (Phase 2 추가)

7. `Consultation`/`Treatment`/`TreatmentCategory`/`StaffSchedule`/`Statistics`/`Dashboard`는 Phase 1 문서화 당시 누락되어 있었고, 이번 Phase 2에서 처음 문서화되었다(§8~§12).
8. `StaffSchedule` 등록/수정은 Phase 2부터 동일 관리자+날짜 중복 등록을 409로 차단한다 — 이전에는 중복 등록이 가능했던 버그였으니 프론트에서 "이미 등록된 일정" 케이스(409) 처리를 새로 추가해야 한다.
9. ~~`Review`의 작성자 본인 확인이 요청 바디의 `userId` 값 비교로 이루어짐(JWT 인증 미완료 상태의 임시 구현, 다른 사용자 명의 도용 가능)~~ → 해결됨. 이제 작성/수정/삭제 모두 JWT 인증 주체(`Authentication.getName()`)로 작성자를 식별하며, 요청 바디의 `userId`는 더 이상 받지 않는다(§13 참고).
10. `Consultation`/`Treatment`/`TreatmentCategory`의 필수값 누락은 `@Valid`(400, 필드별 에러 메시지 포함)로 검증되고, 서비스 레벨의 상태값 화이트리스트 검증(예: `consultationStatus`가 RECEIVED/SCHEDULED/... 중 하나인지)은 `GlobalExceptionHandler`의 `IllegalArgumentException` -> 400 매핑으로 처리된다(단일 메시지, 필드별 에러 목록은 없음). 해결됨 — 이전 버전 문서의 "500(알려진 이슈)" 표기는 폐기.
