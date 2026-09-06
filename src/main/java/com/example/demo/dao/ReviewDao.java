package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.entity.Review;

/**
 * 파일명: ReviewDao.java
 * 설명: 리뷰(후기) 관련 DAO 인터페이스 (사용자/관리자 공용 — 조회수 증가, 숨김 처리 포함)
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | Review 도메인 신규 생성 (Phase 2)
 */
@Mapper
public interface ReviewDao {

    // 리뷰 등록
    public int insertReview(Review review);

    // 리뷰 목록 조회 (숨김 처리된 리뷰 제외 — 사용자용)
    public List<Review> selectAllReviews();

    // 관리자용 리뷰 목록 조회 (숨김 처리된 리뷰 포함)
    public List<Review> selectAllReviewsForAdmin();

    // 진료 항목별 리뷰 목록 조회 (숨김 처리된 리뷰 제외 — 사용자용)
    public List<Review> selectReviewsByTreatmentId(Integer treatmentId);

    // 리뷰 상세 조회
    public Review selectReviewById(Integer reviewId);

    // 리뷰 조회수 증가
    public int incrementHits(Integer reviewId);

    // 리뷰 수정
    public int updateReview(Review review);

    // 리뷰 숨김 여부 변경 (관리자 모더레이션)
    public int updateReviewHidden(@Param("reviewId") Integer reviewId, @Param("isHidden") Boolean isHidden);

    // 리뷰 삭제
    public int deleteReview(Integer reviewId);
}
