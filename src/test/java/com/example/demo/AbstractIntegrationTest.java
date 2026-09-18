package com.example.demo;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * 파일명: AbstractIntegrationTest.java
 * 설명: Testcontainers로 띄운 실제 PostgreSQL을 대상으로 하는 연동테스트 공통 베이스.
 *       spring.sql.init.mode=always(application.properties)가 컨테이너 기동 시 schema.sql을
 *       그대로 실행하므로, 운영 스키마와 MyBatis 매퍼 XML의 정합성을 실제로 검증할 수 있다.
 *       각 테스트는 @Transactional로 감싸 커밋 후 롤백되므로 테스트 간 데이터가 섞이지 않는다.
 *       @AutoConfigureMockMvc를 베이스에 두어 컨트롤러 MockMvc 테스트도 이 클래스를 상속해서
 *       "진짜" SecurityFilterChain(+ 실제 DB로 뜬 전체 컨텍스트) 위에서 실행되도록 한다
 *       — 순수 @WebMvcTest 슬라이스에서 유효한 JWT를 보내도 인가가 거부되던 프레임워크
 *       버전 조합 이슈(docs/test-report.md 트러블슈팅 항목 참고)를 우회하기 위함이다.
 *
 *       컨테이너는 일부러 @Testcontainers/@Container(JUnit5 확장의 자동 lifecycle 관리)를
 *       쓰지 않고 static 초기화 블록에서 직접 시작한다 — @Testcontainers는 static 필드라도
 *       "이 테스트 클래스"의 afterAll에서 컨테이너를 정지시키므로, 여러 서브클래스가 상속받아
 *       공유하는 상황에서는 매 클래스가 끝날 때마다 컨테이너가 죽어 다음 클래스부터는 전부
 *       연결 실패로 깨진다(Testcontainers 공식 문서의 "Singleton container" 패턴이 바로 이
 *       문제를 피하기 위한 것). Ryuk이 JVM 종료 시 정리해주므로 명시적 stop()은 필요 없다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 인프라 최초 생성
 * 2026-09-07 | 테스트 | @WebMvcTest 슬라이스에서 SecurityContext가 다운스트림 필터에 전파되지
 *                        않는 문제를 발견 — 전체 컨텍스트(@AutoConfigureMockMvc) 기반으로 전환
 * 2026-09-07 | 테스트 | 매퍼 테스트를 여러 클래스로 나눠 실행하면 두 번째 클래스부터 전부 DB
 *                        연결 실패로 깨지는 문제 발견 — @Testcontainers가 클래스 단위로
 *                        컨테이너를 정지시키는 것이 원인이었음. Singleton container 패턴으로 전환
 * 2026-09-07 | 테스트 | 전체 테스트를 한 번에 돌리면 "FATAL: sorry, too many clients already"로
 *                        광범위하게 실패하는 문제 발견 — 이 세션에서 만든 연동테스트가 서로 다른
 *                        @MockitoBean 조합마다 별도의 Spring 컨텍스트(+ HikariCP 풀)를 만드는데,
 *                        컨텍스트가 20개 넘게 쌓이면 풀 합계가 Postgres 기본 max_connections(100)를
 *                        넘어선다. 컨테이너 자체의 max_connections를 올리고 테스트용 Hikari 풀
 *                        크기를 줄여서 해결
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public abstract class AbstractIntegrationTest {

    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("yedocb_test")
            .withUsername("yedocb_test")
            .withPassword("yedocb_test")
            .withCommand("postgres", "-c", "max_connections=300");

    static {
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void overrideDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        // 이 세션의 연동테스트는 서로 다른 @MockitoBean 조합마다 별도 Spring 컨텍스트를 만들고,
        // 각 컨텍스트가 자기 몫의 HikariCP 풀을 연다. 기본 풀 크기(10)로는 컨텍스트가 여러 개
        // 쌓였을 때 Postgres 기본 max_connections(100)를 넘기므로 테스트용으로 풀을 줄인다.
        registry.add("spring.datasource.hikari.maximum-pool-size", () -> "3");
    }
}
