package com.example.demo.dto.response.statistics;

import lombok.Data;

/**
 * 파일명: ConsultationStatisticsResponseDto.java
 * 설명: 관리자 상담 통계 응답 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-25 | 규민 | 클래스 생성
 */

@Data
public class ConsultationStatisticsResponseDto {
    // 전체 상담 수
    private Integer totalConsultationCount;
    // 접수 상담 수
    private Integer receivedCount;
    // 예약 예정 상담 수
    private Integer scheduledCount;
    // 완료 상담 수
    private Integer completedCount;
    // 예약 전환 상담 수
    private Integer convertedCount;
    // 취소 상담 수
    private Integer canceledCount;
    // 예약 전환율
    private Double conversionRate;
}
