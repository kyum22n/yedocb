package com.example.demo;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

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
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 인프라 최초 생성
 * 2026-09-07 | 테스트 | @WebMvcTest 슬라이스에서 SecurityContext가 다운스트림 필터에 전파되지
 *                        않는 문제를 발견 — 전체 컨텍스트(@AutoConfigureMockMvc) 기반으로 전환
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@Transactional
public abstract class AbstractIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("yedocb_test")
            .withUsername("yedocb_test")
            .withPassword("yedocb_test");

    @DynamicPropertySource
    static void overrideDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }
}
