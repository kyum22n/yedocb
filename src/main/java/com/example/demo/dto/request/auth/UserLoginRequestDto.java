package com.example.demo.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 파일명: UserLoginRequestDto.java
 * 설명: 사용자 로그인 요청 DTO
 */
@Data
public class UserLoginRequestDto {
    @NotBlank
    private String uId;

    @NotBlank
    private String uPwd;
}
