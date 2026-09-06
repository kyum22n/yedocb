# Architecture After Phase 2

대상 리포지토리: `C:\kyum\project\yedocb` (package root `com.example.demo`)

이 문서는 Phase 1 + Phase 2 리팩토링이 모두 끝난 시점의 백엔드 최종 구조를 정리한다. 이 코드베이스를 처음 보는 사람이 "어떤 기능이 어느 파일에 있는지"를 이 문서 하나로 찾아갈 수 있게 하는 것이 목표다.

---

## 1. 레이어링 컨벤션

```
HTTP 요청
   │
   ▼
Controller (@RestController)          — 경로/HTTP 메소드 매핑, @Valid로 입력 검증 트리거, ResponseEntity<ExactDtoType> 반환
   │  (Request DTO)
   ▼
Service (@Service)                    — 비즈니스 로직, 존재 확인/권한 확인, Entity ↔ Response DTO 매핑
   │  (Entity)
   ▼
DAO (MyBatis @Mapper 인터페이스)        — SQL 매핑 인터페이스, 구현체 없음 (MyBatis가 프록시 생성)
   │
   ▼
Mapper XML (resources/mapper/*.xml)   — 실제 SQL (PostgreSQL)
```

핵심 컨벤션 요약:

- **DTO 매핑은 정적 팩토리 메소드로 한다.** `XxxResponseDto.from(entity)` 형태. 필드별 setter를 서비스 코드에 나열하지 않는다. (Phase 1 `UserMypageResponseDto.from(User)`가 최초 도입, Phase 2에서 Consultation/Treatment/TreatmentCategory/Review까지 확장)
- **입력 검증은 3단계로 나뉜다**:
  1. Bean Validation(`@NotBlank`, `@NotNull` 등 + 컨트롤러의 `@Valid`) — 형식적 필수값 검증. 위반 시 `MethodArgumentNotValidException` → 400 + `fieldErrors` 목록.
  2. 비즈니스 규칙 검증(존재 확인, 중복 확인, 권한 확인) — `com.example.demo.exception`의 커스텀 예외 사용 (`ResourceNotFoundException`=404, `DuplicateResourceException`=409, `UnauthorizedActionException`=403).
  3. 상태값 화이트리스트 검증 등 서비스 레벨의 순수 입력 검증 — `IllegalArgumentException`을 던지며, `GlobalExceptionHandler`가 이를 400으로 매핑한다(전용 커스텀 예외는 아니므로 필드별 `fieldErrors` 없이 단일 메시지로 응답).
- **예외 처리는 `@RestControllerAdvice` 하나로 집중한다.** `GlobalExceptionHandler`가 모든 예외를 가로채 `ErrorResponse`(status/message/timestamp/fieldErrors) 형태로 응답한다. 정상 응답에는 공용 래퍼(`ApiResponse<T>` 같은)를 쓰지 않는다 — 컨트롤러는 `ResponseEntity<ExactDtoType>`을 그대로 반환한다.
- **인증은 JWT 기반 Stateless.** `SecurityConfig` + `JwtAuthenticationFilter` + `JwtTokenProvider`. permitAll 대상 경로는 `security/SecurityPaths`(전체 permitAll `PUBLIC_PATTERNS`, GET 전용 permitAll `PUBLIC_GET_PATTERNS`) 한 곳에서만 관리한다.
- **DB 스키마는 `schema.sql`에 `CREATE TABLE IF NOT EXISTS`로만 정의한다** (DROP 없음, additive). PostgreSQL 문법.

---

## 2. 도메인별 파일 위치 지도

아래 표는 "이 기능을 고치려면 어떤 파일들을 봐야 하는가"를 도메인별로 정리한 것이다.

| 도메인 | Entity | DAO / Mapper XML | Service | Controller | Request DTO 패키지 | Response DTO 패키지 |
|---|---|---|---|---|---|---|
| User (회원) | `entity/User.java` | `UserDao` / `UserMapper.xml` | `UserService` | `UserController` (`/api/user`) | `dto/request/user` | `dto/response/user` |
| AdminUser (관리자 화면의 회원 관리) | `entity/User.java` | `UserDao` | `AdminUserService` | `AdminUserController` (`/api/admin/user`) | `dto/request/user` | `dto/response/user` |
| Admin (관리자 계정) | `entity/Admin.java` | `AdminDao` / `AdminMapper.xml` | `AdminService` | `AdminController` (`/admin`) | `dto/request/admin` | `dto/response/admin` |
| Reservation (예약) | `entity/Reservation.java` | `ReservationDao`, `AdminReservationDao` / `ReservationMapper.xml`, `AdminReservationMapper.xml` | `ReservationService`, `AdminReservationService` | `ReservationController` (`/reservations`), `AdminReservationController` (`/admin/reservations`) | `dto/request/reservation` | `dto/response/reservation` |
| NoticeEvent (공지/이벤트) | `entity/Notice.java` | `NoticeDao`, `AdminNoticeDao` / `NoticeMapper.xml`, `AdminNoticeMapper.xml` | `NoticeService`, `AdminNoticeService` | `NoticeController` (`/notices`), `AdminNoticeController` (`/admin/notices`) | `dto/request/notice` | `dto/response/notice` |
| Inquiry (1:1 문의) | `entity/Inquiry.java`, `entity/InquiryAnswer.java` | `InquiryDao`, `AdminInquiryDao` / `InquiryMapper.xml`, `AdminInquiryMapper.xml` | `InquiryService`, `AdminInquiryService` | `InquiryController` (`/inquiries`), `AdminInquiryController` (`/admin/inquiries`) | `dto/request/inquiry` | `dto/response/inquiry` |
| **Consultation (상담)** | `entity/Consultation.java` | `ConsultationDao`, `AdminConsultationDao` / `ConsultationMapper.xml`, `AdminConsultationMapper.xml` | `ConsultationService`, `AdminConsultationService` | `ConsultationController` (`/consultations`), `AdminConsultationController` (`/admin/consultations`) | `dto/request/consultation` | `dto/response/consultation` |
| **Treatment (진료항목)** | `entity/Treatment.java` | `TreatmentDao`, `AdminTreatmentDao` / `TreatmentMapper.xml`, `AdminTreatmentMapper.xml` | `TreatmentService`, `AdminTreatmentService` | `TreatmentController` (`/treatments`, GET만·인증불필요), `AdminTreatmentController` (`/admin/treatments`) | `dto/request/treatment` | `dto/response/treatment` |
| **TreatmentCategory (진료항목 카테고리)** | `entity/TreatmentCategory.java` | `TreatmentCategoryDao`, `AdminTreatmentCategoryDao` / `TreatmentCategoryMapper.xml`, `AdminTreatmentCategoryMapper.xml` | `TreatmentCategoryService`, `AdminTreatmentCategoryService` | `TreatmentCategoryController` (`/treatment-categories`, GET만·인증불필요), `AdminTreatmentCategoryController` (`/admin/treatment-categories`) | `dto/request/treatment` | `dto/response/treatment` |
| **StaffSchedule (직원 근무 일정)** | `entity/StaffSchedule.java` | `AdminStaffScheduleDao` / `AdminStaffScheduleMapper.xml` | `AdminStaffScheduleService` | `AdminStaffScheduleController` (`/admin/staff-schedules`, 관리자 전용) | `dto/request/schedule` | `dto/response/schedule` |
| **Statistics (통계/리포트)** | 없음 (순수 집계) | `AdminStatisticsDao` / `AdminStatisticsMapper.xml` | `AdminStatisticsService` | `AdminStatisticsController` (`/admin/statistics`, 관리자 전용) | `dto/request/statistics` | `dto/response/statistics` |
| **Dashboard (관리자 대시보드)** | 없음 (순수 집계) | `AdminDashboardDao` / `AdminDashboardMapper.xml` | `AdminDashboardService` | `AdminDashboardController` (`/admin/dashboard`, 관리자 전용) | - | `dto/response/dashboard` |
| **Review (리뷰/후기) — Phase 2 신규** | `entity/Review.java` | `ReviewDao` / `ReviewMapper.xml` (사용자/관리자 공용, 관리자 전용 쿼리 별도 메소드로 분리) | `ReviewService` (사용자용), `AdminReviewService` (관리자 모더레이션) | `ReviewController` (`/reviews`), `AdminReviewController` (`/admin/reviews`) | `dto/request/review` | `dto/response/review` |
| 인증 (JWT/OAuth) | 없음 (엔티티 없이 토큰 발급만 담당) | 없음 (`UserDao`/`AdminDao` 재사용) | 없음 (컨트롤러가 `JwtTokenProvider` 직접 사용) | `UserLoginController`, `AdminLoginController`, `OAuthController` | `dto/request/auth` | `dto/response/auth` |

---

## 3. 패키지 전체 목록

### `com.example.demo.entity` — 도메인 엔티티 (Lombok `@Data`, MyBatis resultType으로 사용)
`User`, `Admin`, `Reservation`, `Notice`, `Inquiry`, `InquiryAnswer`, `Consultation`, `Treatment`, `TreatmentCategory`, `StaffSchedule`, `Review`

### `com.example.demo.dao` — MyBatis `@Mapper` 인터페이스 (구현 없음, XML과 1:1 대응)
`UserDao`, `AdminDao`, `ReservationDao`, `AdminReservationDao`, `NoticeDao`, `AdminNoticeDao`, `InquiryDao`, `AdminInquiryDao`, `ConsultationDao`, `AdminConsultationDao`, `TreatmentDao`, `AdminTreatmentDao`, `TreatmentCategoryDao`, `AdminTreatmentCategoryDao`, `AdminStaffScheduleDao`, `AdminStatisticsDao`, `AdminDashboardDao`, `ReviewDao`

### `com.example.demo.service` — 비즈니스 로직
`UserService`, `AdminUserService`, `AdminService`, `ReservationService`, `AdminReservationService`, `NoticeService`, `AdminNoticeService`, `InquiryService`, `AdminInquiryService`, `ConsultationService`, `AdminConsultationService`, `TreatmentService`, `AdminTreatmentService`, `TreatmentCategoryService`, `AdminTreatmentCategoryService`, `AdminStaffScheduleService`, `AdminStatisticsService`, `AdminDashboardService`, `ReviewService`, `AdminReviewService`

### `com.example.demo.controller` — REST 엔드포인트
`UserController`, `AdminUserController`, `AdminController`, `ReservationController`, `AdminReservationController`, `NoticeController`, `AdminNoticeController`, `InquiryController`, `AdminInquiryController`, `ConsultationController`, `AdminConsultationController`, `TreatmentController`, `AdminTreatmentController`, `TreatmentCategoryController`, `AdminTreatmentCategoryController`, `AdminStaffScheduleController`, `AdminStatisticsController`, `AdminDashboardController`, `ReviewController`, `AdminReviewController`, `UserLoginController`, `AdminLoginController`, `OAuthController`

### `com.example.demo.dto.request.*` — 요청 DTO (도메인별 하위 패키지)
`user`, `admin`, `reservation`, `notice`, `inquiry`, `consultation`, `treatment`, `schedule`, `statistics`, `review`(신규), `auth`

### `com.example.demo.dto.response.*` — 응답 DTO (도메인별 하위 패키지, 모두 `static from(entity)` 팩토리 보유 — Statistics/Dashboard는 엔티티가 없어 예외)
`user`, `admin`, `reservation`, `notice`, `inquiry`, `consultation`, `treatment`, `schedule`, `statistics`, `dashboard`, `review`(신규), `auth`

### `com.example.demo.config` — 스프링 설정
`PasswordEncoderConfig`(BCrypt 빈), `SecurityConfig`(JWT Stateless 인가 규칙), `CorsProperties`(CORS 오리진 외부화)

### `com.example.demo.security` — 인증/인가 인프라
`SecurityPaths`(permitAll 화이트리스트 단일 출처), `JwtTokenProvider`(토큰 발급/검증), `JwtAuthenticationFilter`(요청마다 토큰 검증 후 `SecurityContextHolder`에 인증 정보 저장)

### `com.example.demo.exception` — 전역 예외 처리
`ResourceNotFoundException`(404), `DuplicateResourceException`(409), `UnauthorizedActionException`(403), `InvalidVerificationCodeException`(400), `InvalidCredentialsException`(401), `ErrorResponse`(공통 에러 응답 레코드), `GlobalExceptionHandler`(`@RestControllerAdvice`)

---

## 4. 리소스 파일

- `src/main/resources/schema.sql` — 전체 테이블 정의(Phase 1: `users`, `admin`, `reservation`, `notice`, `inquiry`, `inquiry_answer` / Phase 2 추가: `treatment_category`, `treatment`, `consultation`, `staff_schedule`, `review`)
- `src/main/resources/mapper/*.xml` — 도메인별 MyBatis SQL 매퍼 (DAO 인터페이스와 1:1 대응, 파일명 규칙은 `{DaoName}.xml`)
- `src/main/resources/application.properties` — DB 연결, JWT 시크릿/만료시간, OAuth 클라이언트 설정, `spring.sql.init.mode=always`

## 5. 테스트

`src/test/java/com/example/demo/service/` — 서비스 레이어 단위 테스트(Mockito, 실제 DB 미사용): `UserServiceTest`, `AdminUserServiceTest`, `ReviewServiceTest`, `AdminStaffScheduleServiceTest`, `AdminStatisticsServiceTest`
`src/test/java/com/example/demo/exception/` — `GlobalExceptionHandlerTest`
`src/test/java/com/example/demo/DemoApplicationTests` — 스프링 컨텍스트 로드 테스트(로컬에 PostgreSQL이 없으면 실패 — 이 저장소를 로컬에서 검증할 때 항상 나타나는 사전 존재 이슈이며 코드 결함이 아니다)
