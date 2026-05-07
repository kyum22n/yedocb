package com.example.demo.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.Data;

/**
 * 파일명: Consultation.java
 * 설명: 상담 정보 관련 엔티티
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 엔티티 추가
 */

@Data
public class Consultation {
    // 상담 ID
    private Integer consultationId;
    // 사용자 ID
    private Integer memberId;
    // 예약 ID
    private Integer reservationId;
    // 진료 항목 ID
    private Integer treatmentId;
    // 담당자 ID
    private Integer adminId;
    // 상담 처리 상태
    private String consultationStatus;
    // 상담 메모
    private String consultationMemo;
    // 상담 일자
    private LocalDate preferredDate;
    // 상담 시간
    private LocalTime preferredTime;
    // 생성일
    private LocalDateTime createdAt;
    // 수정일
    private LocalDateTime updatedAt;
}
