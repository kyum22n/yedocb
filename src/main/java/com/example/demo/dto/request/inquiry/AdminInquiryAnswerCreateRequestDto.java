package com.example.demo.dto.request.inquiry;

import lombok.Data;

/**
 * 파일명: AdminInquiryAnswerCreateRequestDto.java
 * 설명: 관리자 문의 답변 등록 요청 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 클래스 생성
 */

@Data
public class AdminInquiryAnswerCreateRequestDto {
    // 문의 ID
    private Integer inquiryId;
    // 답변자 ID
    private Integer adminId;
    // 답변 내용
    private String answerContent;
}
