package com.example.demo.dto.response.reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.Data;

/**
 * 파일명: AdminReservationResponseDto.java
 * 설명: 관리자 예약 응답 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 클래스 생성
 */

@Data
public class AdminReservationResponseDto {
    // 예약 ID
    private Integer reservationId;
    // 예약자 ID
    private String uId;
    // 진료 항목 ID
    private Integer treatmentId;
    // 담당자 ID
    private Integer adminId;
    // 예약 일자
    private LocalDate reservationDate;
    // 예약 시간
    private LocalTime reservationTime;
    // 예약 상태
    private String reservationStatus;
    // 고객 메모
    private String memberMemo;
    // 담당자 메모
    private String adminMemo;
    // PMS 연동 상태
    private String pmsSyncStatus;
    // 생성일
    private LocalDateTime createdAt;
    // 수정일
    private LocalDateTime updatedAt;
}
