package com.example.demo.dto.request.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 파일명: ReviewCreateRequestDto.java
 * 설명: 리뷰(후기) 등록 요청 DTO. 작성자(userId)는 클라이언트가 지정하지 않고
 *       서버가 JWT 인증 주체(SecurityContextHolder)에서 직접 가져온다 — 요청 바디의
 *       임의 userId를 신뢰해 다른 사용자 명의로 리뷰를 작성할 수 있던 문제를 막기 위함.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | Review 도메인 신규 생성 (Phase 2)
 * 2026-09-06 | 리팩토링 | userId 필드 제거 (요청 바디 스푸핑 방지, 알려진 이슈 수정)
 */
@Data
public class ReviewCreateRequestDto {
    // 시술/진료 항목 ID
    @NotNull
    private Integer treatmentId;
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
