package com.example.demo.dto.request.member;

import lombok.Data;

/**
 * 파일명: MemberMypageUpdateRequestDto.java
 * 설명: 사용자 마이페이지 정보 수정 요청 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 클래스 생성
 */

@Data
public class MemberMypageUpdateRequestDto {
    // 회원 ID
    private Integer memberId;
    // 회원 이름
    private String memberName;
    // 회원 이메일
    private String memberEmail;
    // 회원 전화번호
    private String memberPhone;
    // 회원 생년월일
    private LocalDate memberBirth;
    // 회원 성별
    private String memberGender;
}
