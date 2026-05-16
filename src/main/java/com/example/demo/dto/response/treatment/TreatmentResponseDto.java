package com.example.demo.dto.response.treatment;

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
}
