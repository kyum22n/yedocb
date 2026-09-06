package com.example.demo.dto.request.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 파일명: AdminCreateRequestDto.java
 * 설명: 관리자 생성 요청 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | DTO 추가
 * 2026-09-06 | 리팩토링 | 검증 어노테이션 추가, 컨트롤러에 실제 연결 (기존에 Admin 엔티티를
 *                        그대로 요청 바디로 받던 알려진 이슈 수정)
 */

@Data
public class AdminCreateRequestDto {
    // 관리자 로그인 ID
    @NotBlank
    private String adminLoginId;
    // 관리자 비밀번호
    @NotBlank
    private String adminPassword;
    // 관리자 이름
    @NotBlank
    private String adminName;
    // 관리자 이메일
    @NotBlank
    @Email
    private String adminEmail;
    // 관리자 전화번호
    private String adminPhone;
    // 관리자 권한 (미지정 시 서비스에서 "ADMIN"으로 기본 설정)
    private String adminRole;
}
