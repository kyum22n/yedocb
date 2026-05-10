package com.example.demo.dto.response.member;

import lombok.Data;

/**
 * 파일명: AdminMemberListResponseDto.java
 * 설명: 관리자 회원 목록 조회 응답 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 클래스 생성
 */

@Data
public class AdminMemberListResponseDto {
    // 회원 ID
    private Integer memberId;
    // 로그인 ID
    private String memberLoginId;
    // 이름
    private String memberName;
    // 이메일
    private String memberEmail;
    // 전화번호
    private String memberPhone;
    // 가입 일자
    private LocalDateTime createdAt;
}
