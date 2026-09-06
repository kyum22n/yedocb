package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.response.review.AdminReviewResponseDto;
import com.example.demo.service.AdminReviewService;

/**
 * 파일명: AdminReviewController.java
 * 설명: 관리자용 리뷰(후기) 관련 controller (모더레이션 — 숨김/삭제)
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | Review 도메인 신규 생성 (Phase 2)
 */
@RestController
@RequestMapping("/admin/reviews")
public class AdminReviewController {

    @Autowired
    private AdminReviewService adminReviewService;

    // 리뷰 목록 조회 (숨김 리뷰 포함)
    @GetMapping("/all")
    public ResponseEntity<List<AdminReviewResponseDto>> getReviewList() {
        return ResponseEntity.ok(adminReviewService.getAllReviews());
    }

    // 리뷰 숨김/노출 전환 (기본값: 숨김 처리)
    @PutMapping("/hide")
    public ResponseEntity<Integer> hideReview(
            @RequestParam("reviewId") Integer reviewId,
            @RequestParam(value = "isHidden", defaultValue = "true") Boolean isHidden) {
        return ResponseEntity.ok(adminReviewService.hideReview(reviewId, isHidden));
    }

    // 리뷰 삭제 (모더레이션)
    @DeleteMapping("/delete")
    public ResponseEntity<Integer> deleteReview(@RequestParam("reviewId") Integer reviewId) {
        return ResponseEntity.ok(adminReviewService.removeReview(reviewId));
    }
}
