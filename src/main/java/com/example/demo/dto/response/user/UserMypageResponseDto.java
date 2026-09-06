package com.example.demo.dto.response.user;

import java.time.LocalDate;

import com.example.demo.entity.User;

import lombok.Data;

/**
 * 파일명: UserMypageResponseDto.java
 * 설명: 사용자 마이페이지 정보 조회 응답 DTO (구 MemberMypageResponseDto 대체)
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | Member -> User 통합 (Phase 1). java.time.LocalDate import 누락 버그 수정,
 *                        from(User) 정적 팩토리 메소드 추가 (uPwd는 절대 포함하지 않음)
 */
@Data
public class UserMypageResponseDto {

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

    public static UserMypageResponseDto from(User entity) {
        UserMypageResponseDto dto = new UserMypageResponseDto();
        dto.setUId(entity.getUId());
        dto.setUName(entity.getUName());
        dto.setUEmail(entity.getUEmail());
        dto.setUPhone(entity.getUPhone());
        dto.setUBirth(entity.getUBirth());
        dto.setUGender(entity.getUGender());
        return dto;
    }
}
