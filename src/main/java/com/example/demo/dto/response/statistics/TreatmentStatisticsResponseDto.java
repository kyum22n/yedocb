package com.example.demo.dto.response.statistics;

import lombok.Data;

/**
 * 파일명: TreatmentStatisticsResponseDto.java
 * 설명: 관리자 시술/진료 항목 통계 응답 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-25 | 규민 | 클래스 생성
 */

@Data
public class TreatmentStatisticsResponseDto {
    // 진료 항목 ID
    private Integer treatmentId;
    // 진료 항목명
    private String treatmentName;
    // 예약 수
    private Integer reservationCount;
}
