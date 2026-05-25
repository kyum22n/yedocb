package com.example.demo.dto.response.statistics;

import lombok.Data;

/**
 * 파일명: InquiryStatisticsResponseDto.java
 * 설명: 관리자 문의 통계 응답 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-25 | 규민 | 클래스 생성
 */

@Data
public class InquiryStatisticsResponseDto {
    // 전체 문의 수
    private Integer totalInquiryCount;
    // 답변 대기 문의 수
    private Integer waitingCount;
    // 답변 완료 문의 수
    private Integer answeredCount;
}
