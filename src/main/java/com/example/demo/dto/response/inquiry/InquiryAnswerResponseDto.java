package com.example.demo.dto.response.inquiry;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 파일명: InquiryAnswerResponseDto.java
 * 설명: 문의 답변 응답 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 클래스 생성
 */

@Data
public class InquiryAnswerResponseDto {
    // 답변 ID
    private Integer answerId;
    // 문의 ID
    private Integer inquiryId;
    // 답변자 ID
    private Integer adminId;
    // 답변 내용
    private String answerContent;
    // 생성일
    private LocalDateTime createdAt;
    // 수정일
    private LocalDateTime updatedAt;
}
