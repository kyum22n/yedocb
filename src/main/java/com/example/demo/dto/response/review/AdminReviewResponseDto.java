package com.example.demo.dto.response.review;

import java.time.LocalDateTime;

import com.example.demo.entity.Review;

import lombok.Data;

/**
 * 파일명: AdminReviewResponseDto.java
 * 설명: 관리자용 리뷰(후기) 응답 DTO. 숨김 처리 모더레이션을 위해 isHidden을 포함한다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | Review 도메인 신규 생성 (Phase 2)
 */
@Data
public class AdminReviewResponseDto {
    // 리뷰 ID
    private Integer reviewId;
    // 시술/진료 항목 ID
    private Integer treatmentId;
    // 작성자(사용자) ID
    private String userId;
    // 제목
    private String title;
    // 내용
    private String content;
    // 이미지 URL
    private String imageUrl;
    // 해시태그
    private String hashTag;
    // 조회수
    private int hits;
    // 숨김 여부
    private Boolean isHidden;
    // 생성일
    private LocalDateTime createdAt;
    // 수정일
    private LocalDateTime updatedAt;

    public static AdminReviewResponseDto from(Review entity) {
        AdminReviewResponseDto dto = new AdminReviewResponseDto();
        dto.setReviewId(entity.getReviewId());
        dto.setTreatmentId(entity.getTreatmentId());
        dto.setUserId(entity.getUserId());
        dto.setTitle(entity.getTitle());
        dto.setContent(entity.getContent());
        dto.setImageUrl(entity.getImageUrl());
        dto.setHashTag(entity.getHashTag());
        dto.setHits(entity.getHits());
        dto.setIsHidden(entity.getIsHidden());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
