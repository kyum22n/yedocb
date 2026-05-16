package com.example.demo.dto.request.inquiry;

import lombok.Data;

/**
 * 파일명: InquiryDeleteRequestDto.java
 * 설명: 사용자 문의 삭제 요청 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 클래스 생성
 */

@Data
public class InquiryDeleteRequestDto {
    // 문의 ID
    private Integer inquiryId;
    // 문의자 ID
    private Integer memberId;
}
