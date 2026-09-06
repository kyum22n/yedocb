package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.review.ReviewCreateRequestDto;
import com.example.demo.dto.request.review.ReviewUpdateRequestDto;
import com.example.demo.dto.response.review.ReviewResponseDto;
import com.example.demo.service.ReviewService;

import jakarta.validation.Valid;

/**
 * 파일명: ReviewController.java
 * 설명: 사용자용 리뷰(후기) 관련 controller
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | Review 도메인 신규 생성 (Phase 2)
 * 2026-09-06 | 리팩토링 | 작성자 식별을 요청 바디 대신 인증 주체(Authentication)에서 획득하도록 수정
 *                        (알려진 이슈 정리 — 요청 바디의 userId를 신뢰하던 인가 우회 문제 해결)
 */
@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // 리뷰 등록 (작성자 = 인증된 본인, JwtAuthenticationFilter가 설정한 Authentication.getName())
    @PostMapping("/register")
    public ResponseEntity<Integer> registerReview(
            @Valid @RequestBody ReviewCreateRequestDto request,
            Authentication authentication) {
        return ResponseEntity.ok(reviewService.createReview(request, authentication.getName()));
    }

    // 리뷰 목록 조회
    @GetMapping("/all")
    public ResponseEntity<List<ReviewResponseDto>> getReviewList() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    // 진료 항목별 리뷰 목록 조회
    @GetMapping("/treatment")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByTreatmentId(
            @RequestParam("treatmentId") Integer treatmentId) {
        return ResponseEntity.ok(reviewService.getReviewsByTreatmentId(treatmentId));
    }

    // 리뷰 상세 조회 (조회수 증가)
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDto> getReviewDetail(@PathVariable("reviewId") Integer reviewId) {
        return ResponseEntity.ok(reviewService.getReviewById(reviewId));
    }

    // 리뷰 수정 (작성자 본인만 가능 - 본인 확인은 인증 주체 기준)
    @PutMapping("/update")
    public ResponseEntity<Integer> updateReview(
            @Valid @RequestBody ReviewUpdateRequestDto request,
            Authentication authentication) {
        return ResponseEntity.ok(reviewService.modifyReview(request, authentication.getName()));
    }

    // 리뷰 삭제 (작성자 본인만 가능 - 본인 확인은 인증 주체 기준)
    @DeleteMapping("/delete")
    public ResponseEntity<Integer> deleteReview(
            @RequestParam("reviewId") Integer reviewId,
            Authentication authentication) {
        return ResponseEntity.ok(reviewService.removeReview(reviewId, authentication.getName()));
    }
}
