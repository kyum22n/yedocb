package com.example.demo.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * 파일명: JwtAuthenticationFilterTest.java
 * 설명: JwtAuthenticationFilter 단위 테스트. Authorization 헤더 파싱, permitAll(SecurityPaths)
 *       조기 통과, 유효/무효 토큰에 따른 SecurityContext 설정 여부를 Spring 컨텍스트 없이 검증한다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성 (SecurityConfig/JwtAuthenticationFilter 슬라이스
 *                        테스트에서 인증이 걸리지 않던 원인 조사 과정에서 함께 작성)
 */
class JwtAuthenticationFilterTest {

    private JwtTokenProvider jwtTokenProvider;
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "secret", "bG9jYWwtZGV2LW9ubHktc2VjcmV0LWNoYW5nZS1tZS0xMjM0NTY3ODkw");
        ReflectionTestUtils.setField(jwtTokenProvider, "validityInMilliseconds", 3600000L);
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshValidityInMilliseconds", 604800000L);
        ReflectionTestUtils.invokeMethod(jwtTokenProvider, "init");

        filter = new JwtAuthenticationFilter(jwtTokenProvider);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void 유효한_Bearer토큰이면_SecurityContext에_인증정보를_설정한다() throws Exception {
        String token = jwtTokenProvider.createToken("admin01", List.of("ADMIN"));

        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "/admin/consultations/convert");
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getName()).isEqualTo("admin01");
        assertThat(authentication.getAuthorities())
                .extracting(Object::toString)
                .containsExactly("ROLE_ADMIN");
    }

    @Test
    void 토큰없이_permitAll이_아닌_경로면_인증정보를_설정하지않는다() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/admin/consultations/all");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void permitAll경로는_토큰없어도_필터체인을_통과한다() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/user/login");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertThat(chain.getRequest()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void 위조된_토큰이면_인증정보를_설정하지않는다() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/admin/consultations/all");
        request.addHeader("Authorization", "Bearer not-a-real-jwt");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
