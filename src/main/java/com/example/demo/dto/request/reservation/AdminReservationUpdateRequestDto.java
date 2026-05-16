package com.example.demo.dto.request.reservation;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;

/**
 * 파일명: AdminReservationUpdateRequestDto.java
 * 설명: 관리자 예약 정보 수정 요청 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 클래스 생성
 */

@Data
public class AdminReservationUpdateRequestDto {
    // 예약 ID
    private Integer reservationId;
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
}
