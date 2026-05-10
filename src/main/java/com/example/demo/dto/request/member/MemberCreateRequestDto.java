package com.example.demo.dto.request.member;

import lombok.Data;

/**
 * 파일명: MemberCreateRequestDto.java
 * 설명: 사용자 회원가입 요청 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 클래스 생성
 */

@Data
public class MemberCreateRequestDto {
    // 로그인 아이디
    private String memberLoginId;
    // 로그인 패스워드
    private String memberPassword;
    // 이름
    private String memberName;
    // 이메일
    private String memberEmail;
    // 전화번호
    private String memberPhone;
    // 생년월일
    private LocalDate memberBirth;
    // 성별
    private String memberGender;
}