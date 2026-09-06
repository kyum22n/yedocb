package com.example.demo.dto.response.admin;

import com.example.demo.entity.Admin;

import lombok.Data;

/**
 * 파일명: AdminListResponseDto.java
 * 설명: 관리자 목록 조회 응답 DTO. adminPassword(해시)를 포함하지 않는다
 *       (GET /admin/list가 Admin 엔티티를 그대로 반환하던 알려진 이슈 수정).
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | DTO 추가
 * 2026-09-06 | 리팩토링 | from(Admin) 정적 팩토리 추가, 컨트롤러에 실제 연결
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

    public static AdminListResponseDto from(Admin admin) {
        AdminListResponseDto dto = new AdminListResponseDto();
        dto.setAdminId(admin.getAdminId());
        dto.setAdminLoginId(admin.getAdminLoginId());
        dto.setAdminName(admin.getAdminName());
        dto.setAdminEmail(admin.getAdminEmail());
        dto.setAdminPhone(admin.getAdminPhone());
        dto.setAdminRole(admin.getAdminRole());
        return dto;
    }
}
