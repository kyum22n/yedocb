package com.example.demo.dto.request.reservation;

import lombok.Data;

/**
 * 파일명: AdminReservationStatusUpdateRequestDto.java
 * 설명: 관리자 예약 상태 변경 요청 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 클래스 생성
 */

@Data
public class AdminReservationStatusUpdateRequestDto {
    // 예약 ID
    private Integer reservationId;
    // 담당자 ID
    private Integer adminId;
    // 예약 상태
    private String reservationStatus;
    // 담당자 메모
    private String adminMemo;
}
