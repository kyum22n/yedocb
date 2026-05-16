package com.example.demo.dto.request.inquiry;

import lombok.Data;

/**
 * 파일명: AdminInquiryStatusUpdateRequestDto.java
 * 설명: 관리자 문의 상태 변경 요청 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 클래스 생성
 */

@Data
public class AdminInquiryStatusUpdateRequestDto {
    // 문의 ID
    private Integer inquiryId;
    // 문의 처리 상태
    private String inquiryStatus;
}
