package com.example.demo.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.example.demo.AbstractIntegrationTest;

/**
 * 파일명: RealServerJwtSmokeTest.java
 * 설명: 실제 내장 톰캣(+ Testcontainers Postgres) 위에서 JWT 인증이 엔드투엔드로 정상 동작하는지
 *       확인하는 회귀 방지용 스모크 테스트. @WebMvcTest 슬라이스에서는 유효한 JWT를 보내도
 *       SecurityContext가 다운스트림 필터에 전파되지 않아 401이 발생하는 프레임워크 버전
 *       조합 이슈가 있었는데(docs/test-report.md 참고), 이 테스트는 그 문제가 슬라이스
 *       테스트에만 국한되고 실제 서버에서는 재현되지 않음을 확인하기 위해 작성했다.
 *       java.net.http.HttpClient를 직접 쓰는 이유: Boot 4의 TestRestTemplate 자동구성이
 *       spring-boot-restclient 모듈 누락으로 실패해 대신 표준 JDK HTTP 클라이언트로 우회했다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성 (WebMvcTest 슬라이스 이슈 조사 과정에서 발견한
 *                        회귀 시나리오를 정식 테스트로 승격)
 */
class RealServerJwtSmokeTest extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void 유효한토큰으로_admin경로에_접근하면_401이아니다() throws Exception {
        String token = jwtTokenProvider.createToken("admin01", List.of("ADMIN"));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/admin/consultations/all"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    void 토큰없이_admin경로에_접근하면_401이다() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/admin/consultations/all"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(401);
    }

    // 발견된 이슈(docs/test-report.md 참고): SecurityConfig에 커스텀 AccessDeniedHandler가 없어
    // 인증은 됐지만 권한이 부족한 요청도 403이 아니라 401(authenticationEntryPoint)로 응답한다.
    // REST 관례상 권한부족은 403이 기대되므로, 실제 동작을 있는 그대로 회귀 테스트로 고정해둔다.
    @Test
    void USER권한토큰으로_admin경로에_접근하면_401이다() throws Exception {
        String token = jwtTokenProvider.createToken("user01", List.of("USER"));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/admin/consultations/all"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(401);
    }
}
