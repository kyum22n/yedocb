package com.example.demo.dto.request.reservation;

import lombok.Data;

/**
 * 파일명: ReservationCancelRequestDto.java
 * 설명: 사용자 예약 취소 요청 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 클래스 생성
 */

@Data
public class ReservationCancelRequestDto {
    // 예약 ID
    private Integer reservationId;
    // 예약자 ID
    private String uId;
}
