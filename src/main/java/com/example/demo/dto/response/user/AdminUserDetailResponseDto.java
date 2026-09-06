package com.example.demo.dto.response.user;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.demo.entity.User;

import lombok.Data;

/**
 * 파일명: AdminUserDetailResponseDto.java
 * 설명: 관리자 회원 상세 조회 응답 DTO (구 AdminMemberDetailResponseDto 대체)
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | Member -> User 통합 (Phase 1). java.time.LocalDate/LocalDateTime import 누락 버그 수정,
 *                        from(User) 정적 팩토리 메소드 추가 (uPwd는 절대 포함하지 않음)
 */
@Data
public class AdminUserDetailResponseDto {
    // 회원 ID (로그인 ID)
    private String uId;
    // 이름
    private String uName;
    // 이메일
    private String uEmail;
    // 전화번호
    private String uPhone;
    // 생년월일
    private LocalDate uBirth;
    // 성별
    private String uGender;
    // 가입 일자
    private LocalDateTime createdAt;
    // 수정 일자
    private LocalDateTime updatedAt;

    public static AdminUserDetailResponseDto from(User entity) {
        AdminUserDetailResponseDto dto = new AdminUserDetailResponseDto();
        dto.setUId(entity.getUId());
        dto.setUName(entity.getUName());
        dto.setUEmail(entity.getUEmail());
        dto.setUPhone(entity.getUPhone());
        dto.setUBirth(entity.getUBirth());
        dto.setUGender(entity.getUGender());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
