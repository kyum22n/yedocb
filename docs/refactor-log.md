# Phase 1 리팩토링 로그

대상 리포지토리: `C:\kyum\project\yedocb` (package `com.example.demo`)
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
