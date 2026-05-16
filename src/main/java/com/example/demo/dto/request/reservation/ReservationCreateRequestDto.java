package com.example.demo.dto.request.reservation;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;

/**
 * 파일명: ReservationCreateRequestDto.java
 * 설명: 사용자 예약 등록 요청 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 클래스 생성
 */

@Data
public class ReservationCreateRequestDto {
    // 예약자 ID
    private Integer memberId;
    // 진료 항목 ID
    private Integer treatmentId;
    // 예약 일자
    private LocalDate reservationDate;
    // 예약 시간
    private LocalTime reservationTime;
    // 고객 메모
    private String memberMemo;
}
