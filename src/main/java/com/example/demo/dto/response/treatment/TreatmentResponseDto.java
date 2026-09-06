package com.example.demo.dto.response.treatment;

import com.example.demo.entity.Treatment;

import lombok.Data;

/**
 * 파일명: TreatmentResponseDto.java
 * 설명: 사용자용 진료항목 응답 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 클래스 생성
 */

@Data
public class TreatmentResponseDto {
    // 항목 ID
    private Integer treatmentId;
    // 항목명
    private String treatmentName;
    // 설명
    private String description;
    // 예약 가능 여부
    private Boolean isReservable;

    public static TreatmentResponseDto from(Treatment entity) {
        TreatmentResponseDto dto = new TreatmentResponseDto();
        dto.setTreatmentId(entity.getTreatmentId());
        dto.setTreatmentName(entity.getTreatmentName());
        dto.setDescription(entity.getDescription());
        dto.setIsReservable(entity.getIsReservable());
        return dto;
    }
}
