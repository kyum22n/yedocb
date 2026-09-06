package com.example.demo.entity;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 파일명: Review.java
 * 설명: 리뷰(후기) 엔티티. Phase 2에서 필드명을 camelCase 전체단어로 재설계하고
 *       userId 타입을 User의 새 PK 타입(String)에 맞췄다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | 필드 재설계 (bId/tId/uId/bTitle/... -> 전체단어, Phase 2)
 */
@Data
public class Review {
    // 리뷰 ID (PK)
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
    // 숨김 여부 (관리자 모더레이션 — 기본값 false, 노출)
    private Boolean isHidden = false;
    // 생성일
    private LocalDateTime createdAt;
    // 수정일
    private LocalDateTime updatedAt;
}
