package com.example.demo.dto.request.reservation;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;

/**
 * 파일명: AdminReservationCreateRequestDto.java
 * 설명: 관리자 예약 등록 요청 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 클래스 생성
 */

@Data
public class AdminReservationCreateRequestDto {
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
}
