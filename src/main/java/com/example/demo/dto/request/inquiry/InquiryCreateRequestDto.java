package com.example.demo.dto.request.inquiry;

import lombok.Data;

/**
 * 파일명: InquiryCreateRequestDto.java
 * 설명: 사용자 문의 등록 요청 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 클래스 생성
 */

@Data
public class InquiryCreateRequestDto {
    // 문의자 ID
    private Integer memberId;
    // 문의 유형
    private String inquiryType;
    // 문의 제목
    private String title;
    // 문의 내용
    private String content;
}
