package com.example.demo.dto.response.statistics;

import java.util.List;

import lombok.Data;

/**
 * 파일명: AdminStatisticsResponseDto.java
 * 설명: 관리자 통계/리포트 통합 응답 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-25 | 규민 | 클래스 생성
 */

@Data
public class AdminStatisticsResponseDto {
    // 예약 통계
    private ReservationStatisticsResponseDto reservationStatistics;
    // 상담 통계
    private ConsultationStatisticsResponseDto consultationStatistics;
    // 문의 통계
    private InquiryStatisticsResponseDto inquiryStatistics;
    // 인기 진료 항목 통계
    private List<TreatmentStatisticsResponseDto> treatmentStatistics;
}
