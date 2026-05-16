package com.example.demo.dto.request.consultation;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;

/**
 * 파일명: ConsultationCreateRequestDto.java
 * 설명: 사용자 상담 등록 요청 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 클래스 생성
 */

@Data
public class ConsultationCreateRequestDto {
    // 사용자 ID
    private Integer memberId;
    // 진료 항목 ID
    private Integer treatmentId;
    // 상담 메모
    private String consultationMemo;
    // 희망 상담 일자
    private LocalDate preferredDate;
    // 희망 상담 시간
    private LocalTime preferredTime;
}
