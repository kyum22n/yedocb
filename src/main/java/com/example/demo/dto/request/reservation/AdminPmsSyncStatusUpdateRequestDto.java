package com.example.demo.dto.request.reservation;

import lombok.Data;

/**
 * 파일명: AdminPmsSyncStatusUpdateRequestDto.java
 * 설명: 관리자 PMS 연동 상태 변경 요청 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 클래스 생성
 */

@Data
public class AdminPmsSyncStatusUpdateRequestDto {
    // 예약 ID
    private Integer reservationId;
    // PMS 연동 상태
    private String pmsSyncStatus;
}
