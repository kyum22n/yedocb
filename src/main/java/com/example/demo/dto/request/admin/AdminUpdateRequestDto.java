package com.example.demo.dto.request.admin;

import lombok.Data;

/**
 * 파일명: AdminUpdateRequestDto.java
 * 설명: 관리자 정보 수정 요청 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | DTO 추가
 */

@Data
public class AdminUpdateRequestDto {
    // 관리자 ID
    private Integer adminId;
    // 관리자 이름
    private String adminName;
    // 관리자 이메일
    private String adminEmail;
    // 관리자 전화번호
    private String adminPhone;
}
