package com.example.demo.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 파일명: TokenResponseDto.java
 * 설명: 로그인/토큰 재발급 공통 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponseDto {
    private String accessToken;
    private String refreshToken;
    private String userId;

    public static TokenResponseDto of(String accessToken, String refreshToken, String userId) {
        return new TokenResponseDto(accessToken, refreshToken, userId);
    }

    // OAuth 로그인은 리프레시 토큰을 발급하지 않는다 (B 원본 동작 유지)
    public static TokenResponseDto ofAccessOnly(String accessToken, String userId) {
        return new TokenResponseDto(accessToken, null, userId);
    }
}
