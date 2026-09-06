# 백엔드 리팩토링 로그 (Phase 1~3)

대상 리포지토리: `C:\kyum\project\yedocb` (package `com.example.demo`)

# Phase 1

작업 범위: Admin, User(구 Member), Reservation, NoticeEvent(Notice), Inquiry 도메인 + Reservation/Consultation FK 타입 정리 + 전역 예외 처리 + schema.sql + 단위 테스트

---

## 1. Member -> User 통합

기존 `Member` 엔티티(테이블 `member`, PK `member_id: Integer`, 로그인 ID는 별도 필드 `member_login_id: String`)를
B 프로젝트의 `User` 구조(테이블 `users`, PK이자 로그인 ID인 `uId: String`)로 완전히 대체했다.
Member 관련 파일은 추가하지 않고 전부 삭제 후 새 이름으로 재작성했다(rename + redesign).

### 필드 매핑 (Before -> After)

| Before (Member)              | After (User)     | 비고                                   |
|-------------------------------|------------------|----------------------------------------|
| `memberId: Integer` (PK)      | (삭제)           | 별도 숫자 PK 폐기                       |
| `memberLoginId: String`       | `uId: String` (PK)| 로그인 ID가 곧 PK가 됨                  |
| `memberPassword: String`      | `uPwd: String`   |                                         |
| `memberEmail: String`         | `uEmail: String` |                                         |
| `memberName: String`          | `uName: String`  |                                         |
| `memberPhone: String`         | `uPhone: String` |                                         |
| `memberBirth: LocalDate`      | `uBirth: LocalDate` |                                      |
| `memberGender: String`        | `uGender: String`|                                         |
| `createdAt` / `updatedAt`     | 동일             | 변경 없음                               |
| 테이블 `member`               | 테이블 `users`   |                                         |

### 삭제된 파일
- `entity/Member.java`
- `dao/MemberDao.java`
- `resources/mapper/MemberMapper.xml`
- `controller/MemberController.java`
- `controller/AdminMemberController.java`
- `service/MemberService.java`
- `service/AdminMemberService.java`
- `dto/request/member/MemberCreateRequestDto.java`
- `dto/request/member/MemberMypageUpdateRequestDto.java`
- `dto/response/member/MemberMypageResponseDto.java`
- `dto/response/member/AdminMemberDetailResponseDto.java`
- `dto/response/member/AdminMemberListResponseDto.java`

### 새로 생성된 파일
- `entity/User.java` (오케스트레이팅 세션에서 사전 작성됨, 본 세션은 참조만 함)
- `dao/UserDao.java`, `resources/mapper/UserMapper.xml`
- `controller/UserController.java` (`/api/user/**`), `controller/AdminUserController.java` (`/api/admin/user/**`)
- `service/UserService.java`, `service/AdminUserService.java`
- `dto/request/user/UserCreateRequestDto.java`, `dto/request/user/UserMypageUpdateRequestDto.java`
- `dto/response/user/UserMypageResponseDto.java`, `dto/response/user/AdminUserDetailResponseDto.java`, `dto/response/user/AdminUserListResponseDto.java`

### API 경로 변경
- `/member/**` -> `/api/user/**`
- `/admin/member/**` -> `/api/admin/user/**`

(다른 Phase 1 도메인의 경로(`/reservations`, `/notices`, `/inquiries`, `/admin/...`)는 이번 리팩토링에서 변경하지 않았다. User 도메인만 B의 `/api/...` 컨벤션에 맞춤 — 로그인/JWT/OAuth 인프라가 이 경로를 기대하기 때문.)

---

## 2. 버그 수정

| # | 파일:위치 (Before) | 문제 | 수정 내용 |
|---|---|---|---|
| 1 | `dto/request/member/MemberCreateRequestDto.java` (구) | `LocalDate memberBirth` 필드를 쓰면서 `java.time.LocalDate` import 누락 → 컴파일 불가 | 신규 `UserCreateRequestDto`에 `import java.time.LocalDate;` 포함하여 재작성 |
| 2 | `dto/request/member/MemberMypageUpdateRequestDto.java` (구) | 동일 (`LocalDate` import 누락) | 신규 `UserMypageUpdateRequestDto`에 import 포함 |
| 3 | `dto/response/member/MemberMypageResponseDto.java` (구) | 동일 (`LocalDate` import 누락) | 신규 `UserMypageResponseDto`에 import 포함 |
| 4 | `dto/response/member/AdminMemberDetailResponseDto.java` (구) | `LocalDate`, `LocalDateTime` import 둘 다 누락 | 신규 `AdminUserDetailResponseDto`에 두 import 모두 포함 |
| 5 | `controller/AdminMemberController.java` (구) | `AdminMemberDetailResponseDto` 등 DTO import 누락, `GET /list`가 `List<Member>`(비밀번호 해시 포함)를 그대로 직렬화하여 반환 | `AdminUserController.getAllUsers()`가 `List<AdminUserListResponseDto>`를 반환하도록 수정 (`AdminUserService.getAllUsers()`가 엔티티를 DTO로 매핑). `AdminUserListResponseDto`에는 `uPwd` 필드 자체가 없음 |
| 6 | `service/AdminService.java:57-59` (구) | `AdminDetailResponseDto.setAdminCreatedAt/setAdminCreatedBy/setAdminUpdatedAt` 호출 — `Admin` 엔티티에는 `getAdminCreatedAt()` 등의 메소드가 존재하지 않음(`getCreatedAt()/getCreatedBy()/getUpdatedAt()`만 존재) → 컴파일 불가 | `admin.getCreatedAt()/getCreatedBy()/getUpdatedAt()`를 호출하도록 수정, `AdminDetailResponseDto`의 `createdAt/updatedAt` 타입을 `String` -> `LocalDateTime`으로 수정 |
| 7 | `service/AdminService.java:99` (구) `modifyAdmin()` | `Admin` 객체를 새로 만들어 `adminDao.updateAdmin(admin)` 호출했으나, `AdminDao.updateAdmin(AdminUpdateRequestDto)`는 `AdminUpdateRequestDto` 타입을 요구 → 컴파일 불가 (타입 불일치) | `request`(`AdminUpdateRequestDto`)를 그대로 `adminDao.updateAdmin(request)`에 전달하도록 단순화 |
| 8 | `controller/AdminTreatmentCategoryController.java:21` (Phase 2, 미존재 클래스 import) | `CategoryDeleteRequestDto`를 import하지만 실제로 사용하지 않고 해당 클래스 파일도 존재하지 않아 전체 빌드가 컴파일조차 되지 않음 | Phase 2 컨트롤러의 로직은 건드리지 않고, 사용되지 않는 dangling import 한 줄만 제거하여 빌드를 통과시킴. (Phase 2 팀이 해당 도메인 삭제 기능을 구현할 때 필요한 DTO를 추가해야 함) |

---

## 3. Reservation / Consultation FK 타입 정리 (Task 3)

`memberId: Integer`를 `uId: String`으로 변경(User의 새 PK 타입에 맞춤). 기계적 rename이며 그 외 필드(`treatmentId`, `adminId`, `reservationId`, `adminMemo`, `pmsSyncStatus` 등)는 손대지 않았다.

- `entity/Reservation.java`, `entity/Consultation.java`
- `dao/ReservationDao.java`, `dao/AdminReservationDao.java`, `dao/ConsultationDao.java`, `dao/AdminConsultationDao.java`
- `resources/mapper/ReservationMapper.xml`, `AdminReservationMapper.xml`, `ConsultationMapper.xml`, `AdminConsultationMapper.xml`
- 관련 request/response DTO 전체 (Reservation*, Consultation*)
- `service/ReservationService.java`, `AdminReservationService.java`, `ConsultationService.java`, `AdminConsultationService.java`
- `controller/ReservationController.java`, `AdminReservationController.java`, `ConsultationController.java`, `AdminConsultationController.java`
- DB 컬럼명도 `member_id` -> `u_id`로 함께 변경 (schema.sql에서 신규로 정의하는 컬럼이므로 자유롭게 통일함)

### 판단: Inquiry도 함께 정리함 (Task 3 지시 범위를 넘어선 판단)

Task 3 지시문은 "Reservation과 Consultation FK만 고치라"고 명시했지만, `Inquiry` 도메인은 Consultation과 달리 **Phase 1 범위**이고, `Inquiry.memberId: Integer`가 새 `users.u_id: String` PK를 참조할 수 없어 그대로 두면 FK 정합성이 깨진다.
지시문 말미의 검증 절차("Phase 2 도메인이 아닌 한 `memberId`/`member_id`가 하나도 남아있지 않아야 한다")도 Inquiry가 정리되어야 함을 시사한다.
따라서 Inquiry도 동일한 방식으로 `memberId: Integer` -> `uId: String`으로 변경했다:
- `entity/Inquiry.java`, `dao/InquiryDao.java`, `dao/AdminInquiryDao.java`
- `resources/mapper/InquiryMapper.xml`, `AdminInquiryMapper.xml`
- `dto/request/inquiry/InquiryCreateRequestDto.java`, `InquiryUpdateRequestDto.java`, `InquiryDeleteRequestDto.java`
- `dto/response/inquiry/InquiryResponseDto.java`, `AdminInquiryResponseDto.java`
- `service/InquiryService.java`, `AdminInquiryService.java`
- `controller/InquiryController.java`, `AdminInquiryController.java`

`resources/mapper/AdminDashboardMapper.xml`(Phase 2, Dashboard 도메인)은 `AdminReservationResponseDto`/`AdminInquiryResponseDto`에 `SELECT member_id, ...`로 매핑하고 있었는데, 위 컬럼명 변경(`u_id`)에 맞춰 **SQL의 컬럼명만** `u_id`로 고쳤다. Dashboard의 컨트롤러/서비스 로직 자체는 전혀 건드리지 않았다.

### 최종 검증
`memberId`, `member_id`, `memberLoginId`, `member_login_id` 문자열을 전체 소스에서 재검색하여 남은 참조가 없음을 확인했다(Phase 2 도메인인 Consultation은 FK 타입만 정리 대상이었고 그 외 로직은 원래대로 남아 있음).

---

## 4. 전역 예외 처리 (Task 4)

`com.example.demo.exception` 패키지 신설:
- `ResourceNotFoundException` -> 404
- `DuplicateResourceException` -> 409
- `UnauthorizedActionException` -> 403
- `InvalidVerificationCodeException` -> 400
- `InvalidCredentialsException` -> 401
- `GlobalExceptionHandler` (`@RestControllerAdvice`): 위 5개 + `MethodArgumentNotValidException`(400, 필드별 에러 목록 포함) + `Exception` 폴백(500)
- `ErrorResponse`: `status`, `message`, `timestamp`, (검증 오류 시) `fieldErrors` 필드를 가진 단순 레코드. **정상 응답에는 공용 `ApiResponse<T>` 래퍼를 도입하지 않는다** — 프로젝트 컨벤션대로 컨트롤러는 `ResponseEntity<ExactDtoType>`을 그대로 반환하며, `ErrorResponse`는 예외 상황 전용의 별도 응답 형태다.

### IllegalArgumentException 교체 범위

지시문 예시("존재하지 않는 회원입니다" -> `ResourceNotFoundException`, "이미 존재하는 ID/이메일입니다" -> `DuplicateResourceException`)에 맞춰, **"존재하지 않는 ~입니다" 패턴**과 **"이미 존재/사용 중인 ~입니다" 패턴**만 커스텀 예외로 교체했다. 대상 서비스: `UserService`, `AdminUserService`, `AdminService`, `NoticeService`, `AdminNoticeService`, `ReservationService`, `AdminReservationService`, `InquiryService`, `AdminInquiryService`.

"~는 필수입니다", "올바르지 않은 ~입니다" 같은 입력 검증성 `IllegalArgumentException`은 이번 5개 커스텀 예외 중 어느 것에도 정확히 대응되지 않아(전용 "잘못된 요청" 예외가 정의되지 않음) **그대로 남겨두었다** — 현재는 `GlobalExceptionHandler`의 `Exception` 폴백(500)으로 처리된다. 이 부분은 Phase 2에서 `@Valid` + Bean Validation으로 이관하거나, 별도의 `InvalidRequestException`(400) 신설을 검토할 것을 제안한다.

Consultation 서비스(`ConsultationService`, `AdminConsultationService`)는 Phase 2 도메인이므로 예외 교체 대상에서 제외했다.

---

## 5. schema.sql (Task 5)

`src/main/resources/schema.sql` 신규 생성 (PostgreSQL 문법, `CREATE TABLE IF NOT EXISTS`만 사용, DROP 없음):
- `users` (User.java 기준)
- `admin` (Admin.java 기준)
- `reservation` (Reservation.java 기준, `u_id` FK -> `users.u_id`)
- `notice` (Notice.java 기준)
- `inquiry` (Inquiry.java 기준, `u_id` FK -> `users.u_id`) + `inquiry_answer` (InquiryAnswer.java 기준)

`application.properties`에 추가:
```
spring.sql.init.mode=always
spring.sql.init.schema-locations=classpath:schema.sql
```

---

## 6. 단위 테스트 (Task 6)

- `src/test/java/com/example/demo/service/UserServiceTest.java`
- `src/test/java/com/example/demo/service/AdminUserServiceTest.java`
- `src/test/java/com/example/demo/exception/GlobalExceptionHandlerTest.java`

Mockito(`@Mock`/`@InjectMocks`, `MockitoExtension`)만 사용하고 실제 DB에는 접근하지 않는다. `./gradlew test` 기준 신규 테스트 19개 전부 통과. 기존 `DemoApplicationTests.contextLoads()`는 로컬에 실행 중인 PostgreSQL이 없어 실패하는데, 이는 이번 변경과 무관한 사전 존재 이슈다(데이터소스 연결 자체가 필요한 컨텍스트 테스트).

---

## 7. 인증/JWT/OAuth (오케스트레이팅 세션에서 병행 작업, 사용자 승인 완료)

B(`com.example.yedocb`)의 검증된 JWT 구조를 그대로 이식하되, 사용자에게 사전 승인받은 3가지 최소 수정을 반영했다.

### 신규 파일
- `config/PasswordEncoderConfig.java` — `BCryptPasswordEncoder` 빈 등록. `UserService`/`AdminUserService`/`AdminService`가 `@Autowired PasswordEncoder`를 주입받지 못해 기동 실패할 수 있었던 버그(알려진 버그 #3)를 해결한다.
- `config/SecurityConfig.java` — JWT 기반 Stateless 인증, 경로별 권한(`/api/admin/**` → ADMIN/SUPERADMIN, `/api/user/**` → USER/ADMIN/SUPERADMIN, 그 외 `anyRequest().authenticated()`).
- `config/CorsProperties.java` — CORS 허용 오리진을 `application.properties`의 `cors.allowed-origins`로 외부화(B는 SecurityConfig 내부에 오리진을 하드코딩하고 있었음).
- `security/SecurityPaths.java` — permitAll 대상 경로 목록의 단일 출처.
- `security/JwtTokenProvider.java` — 액세스/리프레시 토큰 발급·검증 (HS256, `jwt.secret`/`jwt.expiration`/`jwt.refresh-expiration`).
- `security/JwtAuthenticationFilter.java` — `SecurityPaths`를 `AntPathMatcher`로 참조하여 permitAll 여부 판단, 토큰 검증 후 `SecurityContextHolder`에 인증 정보 저장.
- `controller/UserLoginController.java` — `POST /api/user/login`, `POST /api/user/refresh`. 리프레시 토큰은 인메모리 `ConcurrentHashMap`으로 관리(B와 동일한 한계 유지, 아래 트레이드오프 참고).
- `controller/AdminLoginController.java` — `POST /api/admin/login`.
- `controller/OAuthController.java` — `POST /api/oauth2/google`, `POST /api/oauth2/kakao`.
- `dto/request/auth/*`, `dto/response/auth/TokenResponseDto.java` — 로그인/토큰 재발급 전용 DTO (User 도메인 DTO 패키지와 분리하여 병행 작업 시 파일 충돌 방지).

### 인증 최소수정 3가지 (사용자 승인, B 대비 diff)

**① OAuthController — 이메일 검증 체크 추가**
B는 Google `userinfo`/Kakao `kakao_account`에서 이메일만 추출하고 검증 여부를 확인하지 않았다. 아래를 추가했다:
- Google: `userInfo.get("email_verified")`가 `true`가 아니면 401 반환.
- Kakao: `kakaoAccount.get("is_email_verified")`가 `true`가 아니면 401 반환.

**② `/api/hello` permitAll 제거**
B는 `JwtAuthenticationFilter`와 `SecurityConfig` 양쪽에 `/api/hello`를 permitAll로 등록해 두었으나, 51개 B 소스 파일 전체를 검색한 결과 이 경로를 실제로 구현한 컨트롤러가 없었다(죽은 화이트리스트 항목). `SecurityPaths.PUBLIC_PATTERNS`에서 제외했다.

**③ permitAll 화이트리스트 단일 출처 통합**
B는 화이트리스트가 두 곳에 따로 있었고 서로 달랐다:
- `JwtAuthenticationFilter`: 문자열 완전일치 11개 경로 하드코딩.
- `SecurityConfig`: Ant 패턴 기반 permitAll 목록. `/api/oauth2/**`, `/api/noticeEvent/**`, `/api/reserve/disabled-times/**`가 필터 쪽엔 누락되어 있었음(치명적이진 않으나 유지보수 시 혼란 요인).

`security/SecurityPaths.PUBLIC_PATTERNS`에 Ant 패턴 리스트를 한 곳에 정의하고, `SecurityConfig`는 이 목록을 순회하며 `permitAll()`을 등록하고, `JwtAuthenticationFilter`는 `AntPathMatcher`로 동일 목록에 매치되면 조기 반환하도록 변경했다. 필터에 있던 별도의 하드코딩 배열은 완전히 삭제했다. Phase 1 범위상 화이트리스트는 `register`/`login`/`refresh`/`admin/login`/`oauth2/**`만 포함한다(B의 `send-code`/`verify-code`/`find_id`/`find_password`/`noticeEvent`/`reserve/disabled-times`는 해당 기능 자체가 A에 아직 없거나 이번 범위 밖이라 목록에 넣지 않음 — 추가되는 시점에 `SecurityPaths` 한 곳만 수정하면 됨).

### 리프레시 토큰 — 인메모리 유지에 따른 트레이드오프 (코드 변경 없음, 문서화만)

`UserLoginController.refreshTokenStore`(`ConcurrentHashMap<String,String>`)는 애플리케이션 프로세스가 재시작되면 전부 초기화된다. Render 무료 티어처럼 컨테이너가 idle-sleep 후 재시작되거나 재배포되는 환경에서는, 재시작 시점에 발급되어 있던 모든 리프레시 토큰이 무효화되어 사용자가 다시 로그인해야 한다(액세스 토큰은 자체 서명 검증이라 만료 전까지는 영향 없음). B도 동일한 구조이며 코드에 "운영 시 반드시 DB 또는 Redis 사용해야 함" 주석이 있다. 이번 리팩토링 범위에서는 이 구조를 유지하기로 확정했으므로(사용자 지시), Redis/DB 전환 없이 이 한계만 기록한다.

### 패키지 네임스페이스

전체 패키지를 `com.example.yedocb`로 이관하는 안도 검토했으나, 사용자 승인에 따라 기존 `com.example.demo`를 유지했다(변경 범위/위험도 최소화).

---

## Phase 2 리팩토링 로그

작업 범위: Review 도메인 신규 구현, StaffSchedule 중복 등록 버그 수정, Consultation/Treatment/TreatmentCategory 예외·DTO 팩토리 리트로핏, schema.sql 확장, 단위 테스트, 문서화.

### 1. Review 도메인 신규 구현

Phase 1 시점에는 `entity/Review.java`만 존재했고 DAO/Service/Controller/DTO/Mapper가 전혀 없었다(엔티티도 오케스트레이팅 세션이 이번 Phase 2 착수 직전에 새로 작성함). 완전히 새로 만든 도메인이므로 legacy 코드와의 호환을 고려할 필요가 없었고, 처음부터 커스텀 예외(`ResourceNotFoundException`/`UnauthorizedActionException`)와 `static from(entity)` DTO 팩토리 패턴, Bean Validation을 전부 적용했다.

**새로 생성된 파일**
- `entity/Review.java` (필드 재설계는 오케스트레이팅 세션 작업, 본 세션은 `isHidden` 필드만 추가)
- `dao/ReviewDao.java`, `resources/mapper/ReviewMapper.xml`
- `service/ReviewService.java` (사용자용), `service/AdminReviewService.java` (관리자용, 모더레이션)
- `controller/ReviewController.java` (`/reviews`), `controller/AdminReviewController.java` (`/admin/reviews`)
- `dto/request/review/ReviewCreateRequestDto.java`, `ReviewUpdateRequestDto.java`
- `dto/response/review/ReviewResponseDto.java` (사용자용), `AdminReviewResponseDto.java` (관리자용)

**판단: `isHidden` 필드 추가**
지시문이 관리자 리뷰 모더레이션에 "숨김/삭제" 기능을 요구했는데, 엔티티에는 노출 제어 필드가 전혀 없었다. `Notice`/`Treatment`/`TreatmentCategory`가 이미 `isVisible: Boolean` 패턴을 쓰고 있어 그 패턴을 따르되, 리뷰는 기본이 "노출"이고 예외적으로 "숨김" 처리하는 모더레이션 흐름이라 의미상 `isHidden`(기본값 `false`)으로 이름 지었다. 사용자용 조회(`selectAllReviews`, `selectReviewsByTreatmentId`)는 `WHERE is_hidden = false`로 필터링하고, 관리자용은 별도 쿼리(`selectAllReviewsForAdmin`)로 전체(숨김 포함)를 반환한다. 사용자용 응답 DTO(`ReviewResponseDto`)에는 `isHidden`을 아예 넣지 않아 프론트가 실수로 이 필드를 노출할 위험을 원천 차단했다.

**판단: `Review.userId`는 `String`, 왜 `Consultation.uId`와 다른가**
`Review` 엔티티는 Phase 2 착수 직전 오케스트레이팅 세션이 필드명을 "전체 단어" 컨벤션(`userId`)으로 재설계했고, 타입은 Phase 1에서 확정된 `User.uId: String`(로그인 ID가 곧 PK) 기준에 맞췄다. 반면 `Consultation.uId`는 Phase 1 Task 3에서 `memberId: Integer` → `uId: String`으로 기계적 rename만 한 것이라 필드명이 예전 축약 컨벤션(`uId`)에 남아 있다. 즉 두 네이밍 컨벤션이 한 코드베이스에 공존하는데, 이는 의도적 재설계(Review)와 최소 변경 리네임(Consultation)이라는 서로 다른 리팩토링 히스토리를 반영한다. Review 이후 신규 도메인은 `userId` 전체 단어 컨벤션을 따르는 것을 권장한다.

**조회수(hits) 증가 패턴 — 참고할 선례 없음**
코드베이스 전체에서 `hits`/`viewCount`/`increment` 패턴이 Review 이전에는 전혀 없었다(Inquiry/Notice 모두 조회수 개념 자체가 없음). 별도 DAO 메소드 `incrementHits(Integer reviewId)`(단순 `UPDATE ... SET hits = hits + 1`)를 신설하고, `ReviewService.getReviewById()`가 상세 조회 직후 이 메소드를 호출하도록 했다. `updated_at`은 조회수 증가만으로는 갱신하지 않는다(모더레이션/내용 수정과 조회는 별개 이벤트로 취급). 동시성 관점에서 `hits = hits + 1` 형태의 원자적 UPDATE라 애플리케이션 레벨 락 없이도 레이스 컨디션에 안전하다.

**작성자 본인 확인 — `UnauthorizedActionException`**
`ReviewService.modifyReview()`/`removeReview()`는 `review.userId`와 요청의 `userId`가 다르면 403(`UnauthorizedActionException`)을 던진다. 요청 바디에 `userId`를 그대로 받는 방식은 임시방편이며(JWT 인증이 완전히 연동되면 `SecurityContextHolder`에서 인증 주체를 가져와야 함), Phase 1 `UserController`의 동일한 TODO 주석 패턴을 그대로 따랐다.

### 2. StaffSchedule 중복 등록 버그 수정 (Task 2)

**버그**: `AdminStaffScheduleService.createStaffSchedule()`/`modifyStaffSchedule()`가 동일 관리자(`adminId`)의 동일 날짜(`scheduleDate`)에 이미 일정이 존재하는지 검증하지 않아, 한 관리자에게 같은 날짜로 여러 개의(예: WORK와 OFF가 동시에) 일정이 중복 등록될 수 있었다.

**수정 전 (Before)**
```java
// createStaffSchedule() — 유형 검증 후 바로 insert, 중복 체크 없음
if(!request.getScheduleType().equals("WORK") && !request.getScheduleType().equals("OFF")) {
    throw new IllegalArgumentException("올바르지 않은 일정 유형입니다.");
}
StaffSchedule staffSchedule = new StaffSchedule();
...
return staffScheduleDao.insertStaffSchedule(staffSchedule);
```

**수정 후 (After)**
```java
StaffSchedule duplicateSchedule = staffScheduleDao.selectStaffScheduleByAdminIdAndDate(
        request.getAdminId(), request.getScheduleDate());
if(duplicateSchedule != null) {
    throw new DuplicateResourceException("이미 해당 날짜에 등록된 직원 일정이 존재합니다.");
}
```

`modifyStaffSchedule()`에도 동일한 검증을 추가하되, 수정 시에는 조회된 기존 일정이 "지금 수정하려는 그 일정 자신"인 경우까지 중복으로 오판하지 않도록 `scheduleId`가 다를 때만 예외를 던지도록 했다(`!duplicateSchedule.getScheduleId().equals(request.getScheduleId())`).

새로 추가된 DAO 메소드: `AdminStaffScheduleDao.selectStaffScheduleByAdminIdAndDate(Integer adminId, LocalDate scheduleDate)` + `AdminStaffScheduleMapper.xml`의 대응 `<select>`.

같은 김에 지시문대로 "존재하지 않는 직원 일정입니다"(`getStaffScheduleById`/`modifyStaffSchedule`/`removeStaffSchedule`) 3곳의 `IllegalArgumentException`을 `ResourceNotFoundException`으로 교체했다. "~는 필수입니다"/"올바르지 않은 ~입니다" 성격의 순수 입력 검증 예외는 Phase 1 정책(refactor-log.md §4)과 동일하게 `IllegalArgumentException`으로 남겨두었다.

### 3. Consultation / Treatment / TreatmentCategory 예외·DTO 팩토리 리트로핏 (Task 3)

**예외 교체 범위** (Phase 1 §4 정책을 그대로 계승)
- "존재하지 않는 상담입니다" (`ConsultationService`, `AdminConsultationService` 전체 메소드) → `ResourceNotFoundException`
- "존재하지 않는 항목입니다" (`TreatmentService`, `AdminTreatmentService`) → `ResourceNotFoundException`
- "존재하지 않는 카테고리입니다" (`TreatmentCategoryService`, `AdminTreatmentCategoryService`) → `ResourceNotFoundException`
- "~는 필수입니다"/"올바르지 않은 ~입니다" 성격의 입력 검증 예외는 그대로 `IllegalArgumentException`으로 남김 (Statistics/Dashboard도 동일 — 엔티티 조회가 없는 순수 집계 도메인이라 이번 리트로핏 대상에서 제외).
- 이 세 도메인 모두 "중복" 패턴(`DuplicateResourceException` 대상)이 원래 존재하지 않아 해당 교체는 없었다.

**DTO 정적 팩토리 추가** — `dto/response/consultation/*`, `dto/response/treatment/*`의 모든 응답 DTO에 `static XxxResponseDto from(Entity entity)`를 추가하고, 서비스 메소드의 필드별 수동 setter 복사 루프를 전부 `stream().map(XxxResponseDto::from).collect(Collectors.toList())`로 교체했다:
- `ConsultationResponseDto.from(Consultation)`, `AdminConsultationResponseDto.from(Consultation)`
- `TreatmentResponseDto.from(Treatment)`, `AdminTreatmentResponseDto.from(Treatment)`
- `CategoryResponseDto.from(TreatmentCategory)`

**Bean Validation 추가** — Create/Update 요청 DTO에 User 엔티티/`UserCreateRequestDto` 스타일과 동일하게 `@NotBlank`(문자열 필수값)/`@NotNull`(참조/숫자 필수값)을 추가했다. 대상: `dto/request/consultation/*`(7개 전부), `dto/request/treatment/*`(4개 전부), `dto/request/schedule/*`(StaffSchedule Create/Update, Task 2 연장선). 컨트롤러의 `@PostMapping`/`@PutMapping` 메소드 파라미터에 `@Valid`를 추가로 붙였다(`ConsultationController`, `AdminConsultationController`, `AdminTreatmentController`, `AdminTreatmentCategoryController`, `AdminStaffScheduleController`, `ReviewController`).

**판단: `TreatmentController`/`TreatmentCategoryController`(사용자용 GET 전용)는 손대지 않음** — 두 컨트롤러는 조회 전용(GET)이라 `@RequestBody`/`@Valid` 적용 대상 메소드가 없다. `AdminConsultationConvertRequestDto`의 `consultationMemo`는 선택 항목으로 남겨 두었다(예약 전환 시 메모 없이도 전환 가능해야 하는 업무 요구사항으로 판단).

### 4. schema.sql 확장 (Task 5)

기존 Phase 1 테이블 정의는 건드리지 않고 `CREATE TABLE IF NOT EXISTS`로 5개 테이블을 추가했다: `treatment_category`, `treatment`(FK `category_id` → `treatment_category`), `consultation`(FK `u_id` → `users`, `reservation_id` → `reservation`, `treatment_id` → `treatment`), `staff_schedule`, `review`(FK `treatment_id` → `treatment`, `user_id` → `users`, `is_hidden` 컬럼 포함). `staff_schedule.admin_id`는 `Admin`이 회원가입 없이 시드/시딩되는 경우가 있을 수 있어 우선 FK 제약 없이(단순 `INTEGER NOT NULL`) 두었다 — 필요 시 `admin(admin_id)` 참조로 강화할 것을 제안한다.

### 5. 단위 테스트 (Task 6)

- `ReviewServiceTest` — 등록/목록조회/상세조회(조회수 증가 `verify`)/수정·삭제 작성자 본인 확인(`UnauthorizedActionException`)/존재하지 않음(`ResourceNotFoundException`) 케이스
- `AdminStaffScheduleServiceTest` — 중복 등록 시 `DuplicateResourceException`(등록/수정 양쪽), 자기 자신을 그대로 수정할 때는 중복으로 오판하지 않음(회귀 테스트), 존재하지 않는 일정 수정/삭제 시 `ResourceNotFoundException`
- `AdminStatisticsServiceTest` — DAO 결과가 있을 때 노쇼율 계산 검증, DAO가 `null`을 반환할 때 0으로 채워지는지 검증, 종료일 < 시작일일 때 `IllegalArgumentException` 검증

Mockito(`@Mock`/`@InjectMocks`, `MockitoExtension`)만 사용하고 실제 DB에는 접근하지 않는다. `./gradlew test` 기준 Phase 1(19개) + Phase 2(19개) = 38개 테스트 중 `DemoApplicationTests.contextLoads()` 1개만 실패하며(로컬에 PostgreSQL이 없어 발생하는 사전 존재 이슈, 이번 변경과 무관), 나머지는 전부 통과한다.

### 6. SecurityPaths — 판단 완료 (오케스트레이팅 세션에서 반영)

Phase 2 세션이 보류한 판단: `ReviewController`의 GET 3종(`/reviews/all`, `/reviews/treatment`, `/reviews/{reviewId}`)을 `Treatment`/`TreatmentCategory`처럼 로그인 없이도 볼 수 있게 할지 여부. `SecurityPaths.PUBLIC_GET_PATTERNS`에 `"/reviews/**"`를 추가해 GET 요청만 permitAll로 열었다(`POST /reviews/register`, `PUT /reviews/update`, `DELETE /reviews/delete`는 계속 인증 필요 — HTTP 메소드 단위로 permitAll을 걸었기 때문에 쓰기 작업은 영향 없음). 이유: 리뷰는 예약 전 탐색 단계에서 보여지는 마케팅성 콘텐츠(진료항목과 동일한 성격)이며, 계획 문서의 "리뷰: 작성/조회" 사용자 기능 중 "조회"에는 별도 로그인 요구가 명시되어 있지 않다.

또한 통합 과정에서 `SecurityConfig`의 기존 규칙이 `/api/admin/**`만 관리자 권한으로 보호하고 있어, 접두사 없는 기존 경로(`/admin/reservations`, `/admin/staff-schedules` 등)는 로그인만 하면(관리자 권한 없이도) 접근 가능했던 보안 공백을 발견해 `.requestMatchers("/admin/**").hasAnyRole("ADMIN", "SUPERADMIN")` 규칙을 추가로 반영했다.

---

# Phase 3 리팩토링 로그

작업 범위: `application.properties` local/prod 프로파일 분리, `logback.xml` 정리, 커밋된 `logs/` 디렉터리 정리

## 1. application.properties 프로파일 분리

기존 단일 `application.properties`(DB 접속정보 포함)를 3개 파일로 분리했다.

- `application.properties` (공통): 앱 이름, `spring.profiles.active=${SPRING_PROFILES_ACTIVE:local}`(미지정 시 로컬 자동 활성화), 서버 포트(`${PORT:8080}` — Render 등 PaaS의 PORT 환경변수 대응), MyBatis 설정, schema.sql 초기화 설정, JWT/CORS/OAuth 설정(모두 기존처럼 `${ENV_VAR:기본값}` 형태 유지 — 로컬에서는 기본값으로 즉시 동작하고, 배포 환경에서는 환경변수로 재정의됨)
- `application-local.properties` (신규): 로컬 PostgreSQL 접속정보(`localhost:5432/yedocb`, `postgres/postgres` — 실제 운영값이 아니므로 커밋 가능), 로깅 레벨 DEBUG
- `application-prod.properties` (신규): DB 접속정보를 `${DB_URL}`/`${DB_USERNAME}`/`${DB_PASSWORD}`로만 작성(기본값 없음 — 값이 없으면 기동 자체가 실패하도록 의도적으로 설계, 운영에서 값 누락을 조용히 넘기지 않기 위함). 로깅 레벨 INFO/WARN.

## 2. logback-spring.xml 신규 작성

기존 A(`com.example.demo`)에는 logback 설정 파일 자체가 없었다(순수 Spring Boot 기본 콘솔 로깅). B(`com.example.yedocb`, 참고용)의 `logback.xml`에는 `FILE_NAME = "D:\\logs\\application.log"`처럼 절대경로 Windows 전용 하드코딩이 있었고, 로컬/운영 구분 없이 항상 콘솔+롤링파일 양쪽에 기록했다.

A에 새로 작성한 `logback-spring.xml`은 이 문제를 처음부터 배제하는 방식으로 설계했다:
- `local` 프로파일: 콘솔 + 상대경로(`./logs/application.log`, 프로젝트 루트 기준) 롤링 파일. 절대경로/OS 종속 경로 없음.
- `prod` 프로파일: 콘솔(STDOUT) 전용, 파일 로깅 없음 — Render 같은 컨테이너 환경은 파일시스템이 재배포/재시작 시 초기화되므로 파일 로깅이 무의미하기 때문.

## 3. 커밋된 logs/ 디렉터리 정리

`git ls-files`로 확인한 결과 A 저장소에는 커밋된 `logs/` 디렉터리가 애초에 없었다(정리할 대상 없음). 다만 Phase 3에서 로컬 프로파일에 파일 로깅(`./logs/application.log`)을 새로 추가했으므로, 향후 실수로 커밋되는 일이 없도록 `.gitignore`에 `/logs/`를 추가했다. Phase 0에서 이미 삭제한 `hs_err_pid*.log`/`replay_pid*.log`(JVM 크래시 덤프) 패턴도 재발 방지 차원에서 함께 추가했다.

## 4. 실제 로컬 기동 검증

로컬 PostgreSQL(포트 5432, 기존 실행 중이던 인스턴스)에 `yedocb` 데이터베이스를 신규 생성한 뒤, `local` 프로파일로 `./gradlew bootRun`을 실제로 구동해 검증했다:
- 애플리케이션 정상 기동 (schema.sql의 모든 `CREATE TABLE IF NOT EXISTS` 정상 실행, HikariCP 연결 성공, JWT 필터 등록 확인)
- `POST /api/user/register` → `POST /api/user/login` → JWT 발급 확인
- 발급받은 토큰으로 `GET /api/user/mypage` 조회 성공, 응답에 `uPwd` 미포함 확인
- 토큰 없이 `GET /admin/list` 요청 시 401 확인 (Phase 2에서 수정한 `/admin/**` 보호 규칙이 실제로 동작함)
- 토큰 없이 `GET /treatments/all`, `GET /reviews/all` 요청 시 200 확인 (공개 브라우징 permitAll 정상 동작)

이 과정에서 로컬 PostgreSQL의 `template1` 콜레이션 버전 불일치 문제를 우회하기 위해 `CREATE DATABASE yedocb TEMPLATE template0`를 사용했다 — 이는 이 머신의 기존 PostgreSQL 설치 환경 이슈이며 프로젝트 코드와는 무관하다.

---

# 알려진 이슈 정리 (Phase 1~2 문서에 남아있던 이슈 해결)

## 1. `IllegalArgumentException`이 500(Internal Server Error)으로 처리되던 문제

Phase 1~2에서 "필수값 누락"/"올바르지 않은 값" 같은 순수 입력 검증 오류를 `IllegalArgumentException`으로 던지되, 전용 400 예외로 교체하지 않고 `GlobalExceptionHandler`의 `Exception` 폴백(500)에 걸리도록 남겨두었던 이슈. 전 도메인(User/Admin/Reservation/Inquiry/Notice/Consultation/Treatment/TreatmentCategory/StaffSchedule/Statistics/Review)에서 개별적으로 예외 타입을 바꾸는 대신, `GlobalExceptionHandler`에 `@ExceptionHandler(IllegalArgumentException.class)` 하나를 추가해 400으로 매핑했다 — `IllegalArgumentException`은 본질적으로 "잘못된 요청"을 의미하므로, 이 매핑이 적용된 이후에는 이 예외를 던지는 모든 서비스 메소드가 자동으로 400을 반환한다. 별도의 `InvalidRequestException` 클래스를 신설하는 대신 표준 예외를 그대로 매핑하는 방식을 택했다 — 이미 수십 곳에서 `IllegalArgumentException`을 사용 중이라 예외 타입을 일일이 교체하는 것보다 안전하고, 5개 커스텀 예외(`ResourceNotFoundException` 등)와 달리 이 예외는 "타입 자체가 곧 400을 의미"하는 표준 JDK 예외라 매핑 하나로 전 도메인에 일관 적용된다.

## 2. `/admin/list`/`/admin/register`가 `Admin` 엔티티를 그대로 주고받던 문제

- `AdminController.getAllAdmins()`: `List<Admin>` → `List<AdminListResponseDto>`로 변경. `AdminListResponseDto`는 이미 존재했지만(Phase 0 이전 스캐폴드에서 만들어졌으나) 어디에서도 사용되지 않던 죽은 DTO였다 — `from(Admin)` 정적 팩토리를 추가하고 실제로 연결했다. `adminPassword` 필드 자체가 이 DTO에 없어 응답에 비밀번호 해시가 포함될 수 없다.
- `AdminController.registerAdmin()`: `@RequestBody Admin` → `@Valid @RequestBody AdminCreateRequestDto`로 변경. 이 DTO도 이미 존재했지만 미사용 상태였다. `adminLoginId`/`adminPassword`/`adminName`/`adminEmail`에 `@NotBlank`(+이메일 형식) 검증을 추가했다.
- `AdminDetailResponseDto`에도 `from(Admin)` 정적 팩토리를 추가해 `AdminService`의 수동 setter 복사를 제거했다(다른 도메인과 동일한 컨벤션으로 통일).
- `AdminUpdateRequestDto`에도 `@NotNull`/`@NotBlank`/`@Email` 검증을 추가하고 컨트롤러에 `@Valid`를 적용했다.

### 테스트 중 발견한 부가 사항 (수정하지 않음, 기록만)

Admin 도메인 전체가 `/admin/**`로 보호되면서 `/admin/register`도 ADMIN/SUPERADMIN 토큰이 있어야 호출 가능해졌다(Phase 2에서 추가한 보안 수정의 자연스러운 결과). 즉 최초의 SUPERADMIN 계정을 만들 방법이 코드상 없다 — 실제 로컬 검증 중에도 DB에 직접 INSERT하여 부트스트랩 계정을 만들어야 했다. 이는 이번 "알려진 이슈 정리" 범위가 아니라 별도의 배포 준비(Deploy Phase) 항목으로 `docs/api-contract.md`의 Admin 섹션에 참고 메모로 남겼다.

## 3. 실제 동작 검증

로컬 서버를 재기동하여 다음을 curl로 직접 확인했다:
- `POST /admin/register`에 필수값을 빠뜨리면 `400` + `fieldErrors`(필드별 메시지) 응답 확인
- 정상 등록 후 `POST /api/admin/login` → JWT 발급 → `GET /admin/list` 조회 시 `200`이며 응답 JSON에 `adminPassword`/`adminLoginId` 이외의 비밀번호 관련 필드가 전혀 없음을 확인
- `POST /admin/staff-schedules/register`에 잘못된 `scheduleType`("INVALID")을 보내면 기존에는 500이었던 것이 이제 `400` + `"올바르지 않은 일정 유형입니다."` 메시지로 응답됨을 확인 (전역 `IllegalArgumentException` 매핑이 실제로 전 도메인에 적용됨을 증명)

`./gradlew test` 기준 38개 테스트 전원 통과(로컬에 실제 `yedocb` DB가 만들어져 있어 `DemoApplicationTests.contextLoads()`도 이번에는 통과함).

## 4. Review 작성자 인가 우회 취약점 수정 (문서화 이슈가 아니라 실제 보안 결함이었음)

`docs/api-contract.md`에 "알려진 이슈"로 기록되어 있던 항목 중 하나("Review 작성자 확인이 요청 바디의 userId 비교로 이루어짐, JWT 미완료 임시 구현")를 다시 검토한 결과, 이는 단순한 문서 정리 대상이 아니라 **실제 인가 우회(broken access control) 취약점**이었다: `ReviewService.modifyReview`/`removeReview`가 `existingReview.getUserId().equals(요청바디의 userId)`로 본인 확인을 했는데, 이 `userId`는 클라이언트가 자유롭게 지정하는 값이었다. 즉 유효한 JWT만 있으면(자신의 계정으로 로그인한 상태라면) 요청 바디에 **다른 사용자의 `uId`를 그대로 적어 보내는 것만으로** 그 사람 명의의 리뷰를 수정/삭제할 수 있었다(uId는 로그인 아이디라 추측/열거가 어렵지 않음). `createReview`도 마찬가지로 요청 바디의 `userId`를 작성자로 그대로 저장해, 다른 사용자 명의로 리뷰를 작성하는 것도 가능했다.

**수정**: `ReviewCreateRequestDto`/`ReviewUpdateRequestDto`에서 `userId` 필드를 완전히 제거했다. 대신 `ReviewController`의 등록/수정/삭제 메소드가 Spring이 주입하는 `Authentication` 파라미터에서 `authentication.getName()`(= `JwtAuthenticationFilter`가 `SecurityContextHolder`에 설정한 인증 주체, 곧 토큰의 `sub` 클레임)을 가져와 서비스에 전달하도록 바꿨다. `ReviewService.createReview(request, authenticatedUserId)`/`modifyReview(request, authenticatedUserId)`가 이 값을 사용한다(`removeReview`는 기존에도 `String userId` 파라미터를 받고 있어 시그니처는 그대로 두고 호출부만 바꿨다).

**실제 검증**: 로컬 서버에서 `testuser1`으로 로그인해 리뷰를 작성한 뒤(작성자가 실제로 `testuser1`로 저장됨을 `GET /reviews/all` 응답으로 확인), `testuser2`로 로그인한 토큰으로 그 리뷰를 수정 시도 → `403 UnauthorizedActionException` 확인. `testuser1` 본인 토큰으로 수정 시도 → `200` 성공 확인. 요청 바디에 `userId`를 실어 보내도 무시되고 토큰의 신원만 사용됨을 확인.

`ReviewServiceTest`도 새 메소드 시그니처(`createReview(request, userId)`, `modifyReview(request, userId)`)에 맞춰 갱신했으며, `./gradlew test` 38개 전원 통과를 재확인했다.

## 5. 관리자 로그인 응답에 adminId/adminRole 누락 (프론트엔드 세션 문의 대응)

Frontend(yedocf) 세션이 Phase 5 착수 전 기존 화면 재정합 중 발견해 알려온 문제: `POST /api/admin/login` 응답(`TokenResponseDto`)에 `accessToken`/`userId`(=adminLoginId)만 있고 숫자 `adminId`나 `adminRole`이 없었다. 그런데 `AdminReservation*RequestDto.adminId`, `AdminInquiryAnswerCreateRequestDto.adminId`, `AdminConsultation*RequestDto.adminId` 등 여러 관리자 하위 API가 요청 바디에 숫자 `adminId`를 요구하고 있어, 프론트가 로그인 직후 이 값을 알아낼 방법이 없었다(로그인 시 알 수 있는 건 문자열 `adminLoginId`뿐).

**판단**: JWT에서 서버가 자동으로 `adminId`를 채워주는 방식(Review처럼 `Authentication`에서 주체를 가져오는 패턴)도 검토했으나, 대상 엔드포인트가 여러 도메인에 걸쳐 있고 이번 대응은 "로그인 응답 보강"만으로 프론트를 즉시 풀어줄 수 있어 더 단순한 방법을 택했다 — 각 서비스 메소드 시그니처를 바꾸는 대신 로그인 응답 DTO에 필요한 정보를 담아 내려주는 쪽으로 결정했다.

**수정**: 기존 공용 `TokenResponseDto`를 그대로 확장하는 대신, 관리자 전용 `AdminTokenResponseDto { accessToken, adminId, adminLoginId, adminRole }`를 신설해 `AdminLoginController`가 이를 반환하도록 바꿨다(프로젝트 컨벤션상 컨트롤러는 `ResponseEntity<정확한DTO타입>`을 반환해야 하므로, User/OAuth 로그인에는 해당 없는 필드를 공용 DTO에 얹기보다 전용 타입을 만드는 쪽을 택함). `adminRole`은 이미 JWT의 `roles` 클레임에도 들어있지만(프론트가 원한다면 토큰을 디코드해서 꺼낼 수도 있음), 로그인 응답에 평문으로 내려줘서 프론트가 토큰을 디코드하지 않고도 SUPERADMIN 전용 메뉴 분기를 할 수 있게 했다.

로컬 서버에서 `POST /api/admin/login` 실제 호출 → `{"accessToken":"...","adminId":2,"adminLoginId":"admin1","adminRole":"SUPERADMIN"}` 응답 확인, `./gradlew test` 38개 전원 통과 재확인.

## 6. 예약 중복 방지 + 마감 시간대 조회 (프론트엔드 세션 요청 대응, 우선순위 1)

Frontend 세션이 Phase 4b 화면 재정합 중 계약에 없어 제거해뒀던 기능. B의 `SecurityConfig`에 `/api/reserve/disabled-times`가 permitAll로 등록되어 있던 것으로 보아 B에도 이 기능이 있었을 것으로 추정되나, A의 Reservation 도메인은 별도로 구축되어 이 기능이 없었다.

단순히 "이미 예약된 시간 목록 조회" API만 추가하는 데 그치지 않고, **서버 쪽에서 실제로 동일 날짜+시간 중복 예약 자체를 막는 검증**도 함께 추가했다 — 조회 API는 UX(비활성화 표시)만 개선할 뿐, 클라이언트가 그 값을 무시하고 그대로 요청을 보내거나 두 사용자가 동시에 같은 시간을 예약하는 경쟁 상황(race condition)을 막지는 못하기 때문이다.

- `ReservationDao.selectReservedTimesByDate(date)`: 취소(CANCELED)/노쇼(NO_SHOW) 상태를 제외한 예약 시간 목록 조회
- `ReservationDao.existsConflictingReservation(date, time, excludeReservationId)`: 동일 날짜+시간에 유효한 예약이 있는지 확인(수정 시 자기 자신은 제외)
- `ReservationService.createReservation`/`modifyReservation`이 등록/수정 시 이 검증을 거쳐 충돌하면 `DuplicateResourceException`(409)을 던짐
- `GET /reservations/disabled-times?reservationDate=`: 인증 불필요(permitAll GET) — 로그인 전에도 예약 가능 시간을 확인할 수 있어야 하므로 `SecurityPaths.PUBLIC_GET_PATTERNS`에 추가

**실제 검증**: 로컬에서 `POST /reservations/register`로 같은 날짜/시간에 두 번 예약 시도 → 두 번째 요청이 `409 DuplicateResourceException` 확인, 이후 `GET /reservations/disabled-times`에 해당 시간이 포함됨을 확인.

## 7. 마이페이지 비밀번호 변경 기능 부재 (프론트엔드 세션 요청 대응, 우선순위 2) + 마이페이지 인가 우회 문제 함께 정리

Frontend 세션이 요청한 비밀번호 변경 기능(`PUT /api/user/password`)을 추가하면서, `UserController`에 이미 남아있던 TODO("uId를 쿼리 파라미터 대신 인증 주체에서 획득하도록 변경할 것" — 인증 인프라가 아직 없던 시점에 남긴 임시 구현 표시)도 함께 해결했다. Review와 동일한 유형의 문제였다: `GET /api/user/mypage?uId=`, `PUT /api/user/mypage/update`(바디의 `uId`), `DELETE /api/user/withdraw?uId=`가 전부 클라이언트가 지정한 `uId`를 그대로 신뢰하고 있어서, 로그인한 사용자라면 누구든 다른 사용자의 `uId`를 지정해 그 사람의 마이페이지를 열람/수정/탈퇴시킬 수 있었다. 비밀번호 변경 기능을 이 패턴 그대로(요청 바디에 uId) 추가했다면 동일한 취약점이 새로 생기는 셈이라, 이번 기회에 마이페이지 전체를 인증 주체 기반으로 통일했다.

- `UserMypageUpdateRequestDto`에서 `uId` 필드 제거
- `UserController.getMyPage`/`updateMyPage`/`withdrawUser`가 쿼리 파라미터/요청 바디의 `uId` 대신 `Authentication.getName()`을 사용하도록 변경
- `UserService.modifyUser(request, authenticatedUserId)`로 시그니처 변경(`removeUser`는 기존 시그니처 유지, 호출부만 변경)
- 신규 `UserPasswordUpdateRequestDto { currentPwd, newPwd }` + `UserService.changePassword(request, authenticatedUserId)`: 현재 비밀번호를 `PasswordEncoder.matches`로 확인한 뒤에만 변경 허용(불일치 시 `InvalidCredentialsException`, 401)
- `UserDao.updatePassword(uId, encodedPwd)` + `UserMapper.xml` 추가

**실제 검증**: 로컬에서 `GET /api/user/mypage`를 파라미터 없이 토큰만으로 호출 → 정상 조회 확인. 틀린 현재 비밀번호로 변경 시도 → 401 확인. 올바른 현재 비밀번호로 변경 → 200, 이후 새 비밀번호로 재로그인 성공까지 확인. `UserServiceTest`에 비밀번호 변경 관련 테스트 3개 추가, `./gradlew test` 41개 전원 통과 확인.

## 8. 아이디/비밀번호 찾기 (우선순위 3, 미착수)

Frontend 세션이 요청한 세 가지 중 우선순위가 가장 낮은 항목으로, 이번 턴에서는 착수하지 않았다. 이메일 발송 인프라(SMTP 설정, 인증 코드/임시 비밀번호 발급 로직)가 필요하고 UX 설계 결정(아이디를 이메일로 보낼지 화면에 바로 보여줄지, 비밀번호를 임시 비밀번호로 재설정할지 재설정 링크 방식으로 할지)이 남아있어 별도로 다룰 예정이다.
