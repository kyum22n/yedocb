package com.example.demo.dto.request.admin;

import lombok.Data;

/**
 * 파일명: AdminCreateRequestDto.java
 * 설명: 관리자 생성 요청 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | DTO 추가
 */

@Data
public class AdminCreateRequestDto {
    // 관리자 로그인 ID
    private String adminLoginId;
    // 관리자 비밀번호
    private String adminPassword;
    // 관리자 이름
    private String adminName;
    // 관리자 이메일
    private String adminEmail;
    // 관리자 전화번호
    private String adminPhone;
}
