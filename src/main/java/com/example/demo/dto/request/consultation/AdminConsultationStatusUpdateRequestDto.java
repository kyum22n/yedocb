package com.example.demo.dto.request.consultation;

import lombok.Data;

/**
 * 파일명: AdminConsultationStatusUpdateRequestDto.java
 * 설명: 관리자 상담 상태 변경 요청 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 클래스 생성
 */

@Data
public class AdminConsultationStatusUpdateRequestDto {
    // 상담 ID
    private Integer consultationId;
    // 담당자 ID
    private Integer adminId;
    // 상담 처리 상태
    private String consultationStatus;
    // 상담 메모
    private String consultationMemo;
}
