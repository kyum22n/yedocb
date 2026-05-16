package com.example.demo.dto.response.consultation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.Data;

/**
 * 파일명: ConsultationResponseDto.java
 * 설명: 사용자 상담 응답 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 클래스 생성
 */

@Data
public class ConsultationResponseDto {
    // 상담 ID
    private Integer consultationId;
    // 사용자 ID
    private Integer memberId;
    // 예약 ID
    private Integer reservationId;
    // 진료 항목 ID
    private Integer treatmentId;
    // 상담 처리 상태
    private String consultationStatus;
    // 상담 메모
    private String consultationMemo;
    // 희망 상담 일자
    private LocalDate preferredDate;
    // 희망 상담 시간
    private LocalTime preferredTime;
    // 생성일
    private LocalDateTime createdAt;
    // 수정일
    private LocalDateTime updatedAt;
}
