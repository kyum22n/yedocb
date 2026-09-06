package com.example.demo.dto.request.treatment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 파일명: TreatmentUpdateRequestDto.java
 * 설명: 관리자용 진료항목 수정 요청 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 클래스 생성
 */

@Data
public class TreatmentUpdateRequestDto {

    // 항목 ID
    @NotNull
    private Integer treatmentId;
    // 카테고리 ID
    @NotNull
    private Integer categoryId;
    // 항목명
    @NotBlank
    private String treatmentName;
    // 설명
    private String description;
    // 예약 가능 여부
    private Boolean isReservable;
    // 노출여부
    private Boolean isVisible;
    
}
