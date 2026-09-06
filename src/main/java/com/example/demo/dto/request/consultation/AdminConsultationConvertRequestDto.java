package com.example.demo.dto.request.consultation;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 파일명: AdminConsultationConvertRequestDto.java
 * 설명: 관리자 상담 예약 전환 요청 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 클래스 생성
 */

@Data
public class AdminConsultationConvertRequestDto {
    // 상담 ID
    @NotNull
    private Integer consultationId;
    // 예약 ID
    @NotNull
    private Integer reservationId;
    // 담당자 ID
    private Integer adminId;
    // 상담 메모
    private String consultationMemo;
}
