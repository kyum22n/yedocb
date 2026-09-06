package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.ReviewDao;
import com.example.demo.dto.response.review.AdminReviewResponseDto;
import com.example.demo.entity.Review;
import com.example.demo.exception.ResourceNotFoundException;

/**
 * 파일명: AdminReviewService.java
 * 설명: 관리자용 리뷰(후기) 관련 서비스 클래스. 작성자 제한 없이 전체 조회, 숨김 처리, 삭제(모더레이션)를 담당한다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | Review 도메인 신규 생성 (Phase 2)
 */
@Service
public class AdminReviewService {

    @Autowired
    private ReviewDao reviewDao;

    // 리뷰 목록 조회 (숨김 리뷰 포함)
    public List<AdminReviewResponseDto> getAllReviews() {
        return reviewDao.selectAllReviewsForAdmin().stream()
                .map(AdminReviewResponseDto::from)
                .collect(Collectors.toList());
    }

    // 리뷰 숨김/노출 전환 (모더레이션)
    public int hideReview(Integer reviewId, Boolean isHidden) {

        Review existingReview = reviewDao.selectReviewById(reviewId);

        if (existingReview == null) {
            throw new ResourceNotFoundException("존재하지 않는 리뷰입니다.");
        }

        return reviewDao.updateReviewHidden(reviewId, isHidden);
    }

    // 리뷰 삭제 (모더레이션 — 작성자 제한 없음)
    public int removeReview(Integer reviewId) {

        Review existingReview = reviewDao.selectReviewById(reviewId);

        if (existingReview == null) {
            throw new ResourceNotFoundException("존재하지 않는 리뷰입니다.");
        }

        return reviewDao.deleteReview(reviewId);
    }
}
