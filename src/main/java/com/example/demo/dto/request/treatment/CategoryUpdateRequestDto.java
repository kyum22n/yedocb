package com.example.demo.dto.request.treatment;

import lombok.Data;

/**
 * 파일명: CategoryUpdateRequestDto.java
 * 설명: 관리자용 진료항목 카테고리 수정 요청 dto
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 클래스 생성
 */

@Data
public class CategoryUpdateRequestDto {
    // 카테고리 ID
    private Integer categoryId;
    // 카테고리명
    private String categoryName;
    // 노출 여부
    private Boolean isVisible;
}
