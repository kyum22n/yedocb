package com.example.demo.dto.response.statistics;

import lombok.Data;

/**
 * 파일명: ReservationStatisticsResponseDto.java
 * 설명: 관리자 예약 통계 응답 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-25 | 규민 | 클래스 생성
 */

@Data
public class ReservationStatisticsResponseDto {
    // 전체 예약 수
    private Integer totalReservationCount;
    // 대기 예약 수
    private Integer pendingCount;
    // 확정 예약 수
    private Integer confirmedCount;
    // 완료 예약 수
    private Integer completedCount;
    // 취소 예약 수
    private Integer canceledCount;
    // 노쇼 예약 수
    private Integer noShowCount;
    // 노쇼율
    private Double noShowRate;
}
