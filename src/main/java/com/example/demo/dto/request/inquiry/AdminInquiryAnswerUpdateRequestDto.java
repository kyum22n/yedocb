package com.example.demo.dto.request.inquiry;

import lombok.Data;

/**
 * 파일명: AdminInquiryAnswerUpdateRequestDto.java
 * 설명: 관리자 문의 답변 수정 요청 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 클래스 생성
 */

@Data
public class AdminInquiryAnswerUpdateRequestDto {
    // 답변 ID
    private Integer answerId;
    // 답변 내용
    private String answerContent;
}
