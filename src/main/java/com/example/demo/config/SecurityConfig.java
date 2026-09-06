package com.example.demo.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.demo.security.JwtAuthenticationFilter;
import com.example.demo.security.SecurityPaths;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * 파일명: SecurityConfig.java
 * 설명: Spring Security 설정 (JWT + 권한별 경로). permitAll 목록은 SecurityPaths(단일 출처)에서 가져온다
 *       (인증 최소수정 3번째 항목). "/api/hello"는 미구현 엔드포인트라 화이트리스트에서 제거했다
 *       (인증 최소수정 2번째 항목).
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | 최초 생성 (com.example.yedocb 참고 이식)
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsProperties corsProperties;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> {
                SecurityPaths.PUBLIC_PATTERNS.forEach(pattern ->
                    auth.requestMatchers(pattern).permitAll()
                );
                SecurityPaths.PUBLIC_GET_PATTERNS.forEach(pattern ->
                    auth.requestMatchers(HttpMethod.GET, pattern).permitAll()
                );
                auth
                    // "/admin/**"와 "/api/admin/**" 양쪽 모두 관리자 전용으로 보호한다
                    // (기존 5도메인은 "/admin/...", User 도메인만 "/api/admin/..." 경로를 씀 - docs/api-contract.md 참고)
                    .requestMatchers("/admin/**").hasAnyRole("ADMIN", "SUPERADMIN")
                    .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "SUPERADMIN")
                    .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN", "SUPERADMIN")
                    .anyRequest().authenticated();
            })
            .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) ->
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
            ))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(corsProperties.getAllowedOrigins());
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
