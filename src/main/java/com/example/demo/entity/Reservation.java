package com.example.demo.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 파일명: Reservation.java
 * 설명: 예약 정보 관련 엔티티
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 필드 수정
 */

@Data
public class Reservation {
    // 예약 ID
    private Integer reservationId;
    // 예약자 ID
    private Integer memberId;
    // 진료 항목 ID
    private Integer treatmentId;
    // 담당자 ID
    private Integer adminId;
    // 예약 일자
    private LocalDate reservationDate;
    // 예약 시간
    private LocalDateTime reservationTime;
    // 예약 상태
    private String reservationStatus;
    // 고객 메모
    private String memberMemo;
    // 담당자 메모
    private String adminMemo;
    // pms 연동 상태
    private String pmsSyncStatus;
    // 생성일(추가)
    private LocalDateTime createdAt;
    // 수정일(추가)
    private LocalDateTime updatedAt;
}
