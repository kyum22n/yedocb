package com.example.demo.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * 파일명: JwtTokenProviderTest.java
 * 설명: JwtTokenProvider 단위 테스트 (Spring 컨텍스트 없이 순수 POJO 수준에서 발급/검증 왕복 확인).
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성
 */
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "secret", "bG9jYWwtZGV2LW9ubHktc2VjcmV0LWNoYW5nZS1tZS0xMjM0NTY3ODkw");
        ReflectionTestUtils.setField(jwtTokenProvider, "validityInMilliseconds", 3600000L);
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshValidityInMilliseconds", 604800000L);
        ReflectionTestUtils.invokeMethod(jwtTokenProvider, "init");
    }

    @Test
    void 토큰발급후_검증하면_유효하다() {
        String token = jwtTokenProvider.createToken("admin01", List.of("ADMIN"));

        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        assertThat(jwtTokenProvider.getUserId(token)).isEqualTo("admin01");
        assertThat(jwtTokenProvider.getRoles(token)).containsExactly("ADMIN");
    }
}
