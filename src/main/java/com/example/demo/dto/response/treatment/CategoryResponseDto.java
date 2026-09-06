package com.example.demo.dto.response.treatment;

import java.time.LocalDateTime;

import com.example.demo.entity.TreatmentCategory;

import lombok.Data;

/**
 * 파일명: CategoryResponseDto.java
 * 설명: 진료항목 카테고리 응답 dto
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 클래스 생성
 */

@Data
public class CategoryResponseDto {
    // 카테고리 ID
    private Integer categoryId;
    // 카테고리명
    private String categoryName;
    // 노출 여부
    private Boolean isVisible;
    // 생성일
    private LocalDateTime createdAt;
    // 수정일
    private LocalDateTime updatedAt;

    public static CategoryResponseDto from(TreatmentCategory entity) {
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setCategoryId(entity.getCategoryId());
        dto.setCategoryName(entity.getCategoryName());
        dto.setIsVisible(entity.getIsVisible());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
