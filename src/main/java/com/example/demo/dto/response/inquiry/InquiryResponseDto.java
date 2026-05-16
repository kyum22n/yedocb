package com.example.demo.dto.response.inquiry;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 파일명: InquiryResponseDto.java
 * 설명: 사용자 문의 응답 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 클래스 생성
 */

@Data
public class InquiryResponseDto {
    // 문의 ID
    private Integer inquiryId;
    // 문의자 ID
    private Integer memberId;
    // 문의 유형
    private String inquiryType;
    // 문의 제목
    private String title;
    // 문의 내용
    private String content;
    // 문의 처리 상태
    private String inquiryStatus;
    // 답변 정보
    private InquiryAnswerResponseDto answer;
    // 생성일
    private LocalDateTime createdAt;
    // 수정일
    private LocalDateTime updatedAt;
}
