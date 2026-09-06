package com.example.demo.dto.response.admin;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 파일명: AdminDetailResponseDto.java
 * 설명: 관리자 상세 정보 응답 DTO(상세 조회/수정 화면용)
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | DTO 추가
 */

@Data
public class AdminDetailResponseDto {
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
    // 생성자
    private String createdBy;
    // 생성일
    private LocalDateTime createdAt;
    // 수정일
    private LocalDateTime updatedAt;
}
