package com.example.demo.dto.response.user;

import java.time.LocalDateTime;

import com.example.demo.entity.User;

import lombok.Data;

/**
 * 파일명: AdminUserListResponseDto.java
 * 설명: 관리자 회원 목록 조회 응답 DTO (구 AdminMemberListResponseDto 대체)
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | Member -> User 통합 (Phase 1). from(User) 정적 팩토리 메소드 추가.
 *                        기존 AdminMemberController가 List&lt;Member&gt;(비밀번호 해시 포함)를
 *                        그대로 반환하던 버그를 고쳐, 이 DTO 리스트를 반환하도록 함
 */
@Data
public class AdminUserListResponseDto {
    // 회원 ID (로그인 ID)
    private String uId;
    // 이름
    private String uName;
    // 이메일
    private String uEmail;
    // 전화번호
    private String uPhone;
    // 가입 일자
    private LocalDateTime createdAt;

    public static AdminUserListResponseDto from(User entity) {
        AdminUserListResponseDto dto = new AdminUserListResponseDto();
        dto.setUId(entity.getUId());
        dto.setUName(entity.getUName());
        dto.setUEmail(entity.getUEmail());
        dto.setUPhone(entity.getUPhone());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
