package com.example.demo.dto.response.admin;

import lombok.Data;

/**
 * 파일명: AdminListResponseDto.java
 * 설명: 관리자 목록 조회 응답 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | DTO 추가
 */

@Data
public class AdminListResponseDto {
    // 관리자 ID
    private Integer adminId;
    // 관리자 로그인 ID
    private String adminLoginId;
    // 관리자 이름
    private String adminName;
    // 관리자 이메일
    private String adminEmail;
    // 관리자 전화번호
    private String adminPhone;
    // 관리자 권한
    private String adminRole;
}
