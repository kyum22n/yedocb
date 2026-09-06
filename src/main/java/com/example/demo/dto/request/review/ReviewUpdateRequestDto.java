package com.example.demo.dto.request.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 파일명: ReviewUpdateRequestDto.java
 * 설명: 리뷰(후기) 수정 요청 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | Review 도메인 신규 생성 (Phase 2)
 */
@Data
public class ReviewUpdateRequestDto {
    // 리뷰 ID
    @NotNull
    private Integer reviewId;
    // 작성자(사용자) ID — 작성자 본인 확인용
    @NotBlank
    private String userId;
    // 제목
    @NotBlank
    private String title;
    // 내용
    @NotBlank
    private String content;
    // 이미지 URL
    private String imageUrl;
    // 해시태그
    private String hashTag;
}
