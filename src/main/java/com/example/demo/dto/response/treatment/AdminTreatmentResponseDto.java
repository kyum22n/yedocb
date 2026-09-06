package com.example.demo.dto.response.treatment;

import java.time.LocalDateTime;

import com.example.demo.entity.Treatment;

import lombok.Data;

/**
 * 파일명: AdminTreatmentResponseDto.java
 * 설명: 관리자용 진료항목 응답 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 클래스 생성
 */

@Data
public class AdminTreatmentResponseDto {

    // 항목 ID
    private Integer treatmentId;
    // 카테고리 ID
    private Integer categoryId;
    // 항목명
    private String treatmentName;
    // 설명
    private String description;
    // 예약 가능 여부
    private Boolean isReservable;
    // 노출여부
    private Boolean isVisible;
    // 생성일
    private LocalDateTime createdAt;
    // 수정일
    private LocalDateTime updatedAt;

    public static AdminTreatmentResponseDto from(Treatment entity) {
        AdminTreatmentResponseDto dto = new AdminTreatmentResponseDto();
        dto.setTreatmentId(entity.getTreatmentId());
        dto.setCategoryId(entity.getCategoryId());
        dto.setTreatmentName(entity.getTreatmentName());
        dto.setDescription(entity.getDescription());
        dto.setIsReservable(entity.getIsReservable());
        dto.setIsVisible(entity.getIsVisible());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
