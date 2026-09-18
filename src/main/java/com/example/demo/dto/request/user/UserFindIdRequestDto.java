package com.example.demo.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 파일명: UserFindIdRequestDto.java
 * 설명: 아이디 찾기 요청 DTO. 이메일로 회원을 조회해 아이디를 해당 이메일로 발송한다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-18 | 디버깅 | 신규 생성 — 아이디/비밀번호 찾기 기능 구현
 */
@Data
public class UserFindIdRequestDto {
    @NotBlank
    @Email
    private String uEmail;
}
