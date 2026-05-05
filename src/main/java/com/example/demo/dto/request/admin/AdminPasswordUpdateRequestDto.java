package com.example.demo.dto.request.admin;
import lombok.Data;

/**
 * 파일명: AdminPasswordUpdateRequestDto.java
 * 설명: 관리자 비밀번호 수정 요청 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | DTO 추가
 */

@Data
public class AdminPasswordUpdateRequestDto {
    // 관리자 ID
    private Integer adminId;
    // 현재 비밀번호
    private String currentPassword;
    // 새 비밀번호
    private String newPassword;
}
