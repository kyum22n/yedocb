package com.example.demo.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 파일명: UserFindPasswordRequestDto.java
 * 설명: 비밀번호 찾기(재발급) 요청 DTO. 아이디로 회원을 조회해 임시 비밀번호를
 *       등록된 이메일로 발송한다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-18 | 디버깅 | 신규 생성 — 아이디/비밀번호 찾기 기능 구현
 */
@Data
public class UserFindPasswordRequestDto {
    @NotBlank
    private String uId;
}
