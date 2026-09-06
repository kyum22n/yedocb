package com.example.demo.dto.request.consultation;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 파일명: AdminConsultationCreateRequestDto.java
 * 설명: 관리자 상담 등록 요청 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 클래스 생성
 */

@Data
public class AdminConsultationCreateRequestDto {
    // 사용자 ID
    @NotBlank
    private String uId;
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
    // 희망 상담 일자
    private LocalDate preferredDate;
    // 희망 상담 시간
    private LocalTime preferredTime;
}
