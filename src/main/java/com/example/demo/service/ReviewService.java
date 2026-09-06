package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.ReviewDao;
import com.example.demo.dto.request.review.ReviewCreateRequestDto;
import com.example.demo.dto.request.review.ReviewUpdateRequestDto;
import com.example.demo.dto.response.review.ReviewResponseDto;
import com.example.demo.entity.Review;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UnauthorizedActionException;

/**
 * 파일명: ReviewService.java
 * 설명: 사용자용 리뷰(후기) 관련 서비스 클래스. 작성자 본인만 수정/삭제할 수 있다.
 *       작성자 식별은 항상 JWT 인증 주체에서 전달받은 userId를 사용한다(요청 바디의
 *       임의 값이 아님 — 다른 사용자 명의 도용/본인확인 우회 방지).
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | Review 도메인 신규 생성 (Phase 2)
 * 2026-09-06 | 리팩토링 | userId를 요청 바디 대신 인증 주체에서 받도록 수정 (알려진 이슈 정리)
 */
@Service
public class ReviewService {

    @Autowired
    private ReviewDao reviewDao;

    // 리뷰 등록 (작성자 = 인증된 본인)
    public int createReview(ReviewCreateRequestDto request, String authenticatedUserId) {

        Review review = new Review();
        review.setTreatmentId(request.getTreatmentId());
        review.setUserId(authenticatedUserId);
        review.setTitle(request.getTitle());
        review.setContent(request.getContent());
        review.setImageUrl(request.getImageUrl());
        review.setHashTag(request.getHashTag());

        return reviewDao.insertReview(review);
    }

    // 리뷰 목록 조회 (숨김 리뷰 제외)
    public List<ReviewResponseDto> getAllReviews() {
        return reviewDao.selectAllReviews().stream()
                .map(ReviewResponseDto::from)
                .collect(Collectors.toList());
    }

    // 진료 항목별 리뷰 목록 조회 (숨김 리뷰 제외)
    public List<ReviewResponseDto> getReviewsByTreatmentId(Integer treatmentId) {
        return reviewDao.selectReviewsByTreatmentId(treatmentId).stream()
                .map(ReviewResponseDto::from)
                .collect(Collectors.toList());
    }

    // 리뷰 상세 조회 (조회수 증가)
    public ReviewResponseDto getReviewById(Integer reviewId) {

        Review review = reviewDao.selectReviewById(reviewId);

        if (review == null) {
            throw new ResourceNotFoundException("존재하지 않는 리뷰입니다.");
        }

        reviewDao.incrementHits(reviewId);
        review.setHits(review.getHits() + 1);

        return ReviewResponseDto.from(review);
    }

    // 리뷰 수정 (작성자 본인만 가능)
    public int modifyReview(ReviewUpdateRequestDto request, String authenticatedUserId) {

        Review existingReview = reviewDao.selectReviewById(request.getReviewId());

        if (existingReview == null) {
            throw new ResourceNotFoundException("존재하지 않는 리뷰입니다.");
        }

        if (!existingReview.getUserId().equals(authenticatedUserId)) {
            throw new UnauthorizedActionException("본인이 작성한 리뷰만 수정할 수 있습니다.");
        }

        Review review = new Review();
        review.setReviewId(request.getReviewId());
        review.setTitle(request.getTitle());
        review.setContent(request.getContent());
        review.setImageUrl(request.getImageUrl());
        review.setHashTag(request.getHashTag());

        return reviewDao.updateReview(review);
    }

    // 리뷰 삭제 (작성자 본인만 가능)
    public int removeReview(Integer reviewId, String userId) {

        Review existingReview = reviewDao.selectReviewById(reviewId);

        if (existingReview == null) {
            throw new ResourceNotFoundException("존재하지 않는 리뷰입니다.");
        }

        if (!existingReview.getUserId().equals(userId)) {
            throw new UnauthorizedActionException("본인이 작성한 리뷰만 삭제할 수 있습니다.");
        }

        return reviewDao.deleteReview(reviewId);
    }
}
