package com.example.demo.dto.request.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 파일명: ReviewUpdateRequestDto.java
 * 설명: 리뷰(후기) 수정 요청 DTO. 작성자 본인 확인은 요청 바디가 아니라 JWT 인증 주체
 *       (SecurityContextHolder)로 한다 — 요청 바디의 임의 userId를 신뢰해 본인 확인을
 *       우회할 수 있던 문제를 막기 위함.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | Review 도메인 신규 생성 (Phase 2)
 * 2026-09-06 | 리팩토링 | userId 필드 제거 (요청 바디 스푸핑으로 본인확인 우회 가능하던 취약점 수정)
 */
@Data
public class ReviewUpdateRequestDto {
    // 리뷰 ID
    @NotNull
    private Integer reviewId;
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
