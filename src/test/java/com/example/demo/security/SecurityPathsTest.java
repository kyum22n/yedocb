package com.example.demo.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * 파일명: SecurityPathsTest.java
 * 설명: SecurityPaths(permitAll 화이트리스트 단일 출처) 단위 테스트. OAuthController는
 *       내부에서 `new RestTemplate()`으로 Google/Kakao에 실제 네트워크 호출을 하는 구조라
 *       MockMvc로 성공 흐름을 재현할 수 없다(테스트 세션에서 운영 코드를 수정하지 않기로
 *       했으므로 리팩토링하지 않음, docs/test-report.md 참고). 대신 "/api/oauth2/**"가
 *       permitAll 목록에 실제로 등록되어 있는지를 화이트리스트 단위테스트로 대체 검증한다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성
 */
class SecurityPathsTest {

    @Test
    void OAuth_로그인콜백_경로는_permitAll이다() {
        assertThat(SecurityPaths.PUBLIC_PATTERNS).contains("/api/oauth2/**");
    }

    @Test
    void 회원가입_로그인_토큰재발급_경로는_permitAll이다() {
        assertThat(SecurityPaths.PUBLIC_PATTERNS).contains(
                "/api/user/register", "/api/user/login", "/api/user/refresh", "/api/admin/login");
    }

    @Test
    void 진료항목_카테고리_리뷰_예약마감시간_GET조회는_permitAll이다() {
        assertThat(SecurityPaths.PUBLIC_GET_PATTERNS).contains(
                "/treatments/**", "/treatment-categories/**", "/reviews/**", "/reservations/disabled-times");
    }

    @Test
    void 미구현_hello엔드포인트는_permitAll목록에_없다() {
        assertThat(SecurityPaths.PUBLIC_PATTERNS).doesNotContain("/api/hello");
    }
}
