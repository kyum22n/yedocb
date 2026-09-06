package com.example.demo.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 파일명: AdminLoginRequestDto.java
 * 설명: 관리자 로그인 요청 DTO
 */
@Data
public class AdminLoginRequestDto {
    @NotBlank
    private String adminLoginId;

    @NotBlank
    private String adminPassword;
}
