package com.example.demo.dto.request.consultation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 파일명: ConsultationCancelRequestDto.java
 * 설명: 사용자 상담 취소 요청 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 클래스 생성
 */

@Data
public class ConsultationCancelRequestDto {
    // 상담 ID
    @NotNull
    private Integer consultationId;
    // 사용자 ID
    @NotBlank
    private String uId;
}
