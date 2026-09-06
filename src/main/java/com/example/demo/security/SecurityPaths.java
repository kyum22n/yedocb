package com.example.demo.security;

import java.util.List;

/**
 * 파일명: SecurityPaths.java
 * 설명: 인증 없이 접근 가능한 경로(permitAll)의 단일 출처.
 *       SecurityConfig(인가 규칙)와 JwtAuthenticationFilter(토큰 검증 스킵) 양쪽이
 *       이 목록 하나만 참조하도록 통합한다 (인증 최소수정 3번째 항목).
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | 최초 생성 (permitAll 화이트리스트 단일화)
 */
public final class SecurityPaths {

    private SecurityPaths() {
    }

    // Ant 스타일 패턴. SecurityConfig의 permitAll()과 JwtAuthenticationFilter의
    // AntPathMatcher 조기-반환 로직이 동일하게 이 목록을 사용한다.
    // 주의: "/api/hello"는 실제 구현된 엔드포인트가 없는 죽은 화이트리스트 항목이므로 제외한다 (인증 최소수정 2번째 항목).
    public static final List<String> PUBLIC_PATTERNS = List.of(
        "/api/user/register",
        "/api/user/login",
        "/api/user/refresh",
        "/api/admin/login",
        "/api/oauth2/**"
    );

    // GET 요청에 한해서만 인증 없이 허용하는 경로 (노출 진료항목/카테고리 조회 등 예약 전 탐색용).
    // SecurityConfig에서만 사용하며, JwtAuthenticationFilter는 이 목록에 대해 별도 처리가 필요 없다
    // (permitAll이라도 필터 자체는 그대로 통과시키고, 토큰이 없으면 인증 정보만 설정하지 않을 뿐이다).
    public static final List<String> PUBLIC_GET_PATTERNS = List.of(
        "/treatments/**",
        "/treatment-categories/**",
        "/reviews/**"
    );
}
