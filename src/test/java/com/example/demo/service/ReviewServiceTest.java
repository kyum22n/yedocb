package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.dao.ReviewDao;
import com.example.demo.dto.request.review.ReviewCreateRequestDto;
import com.example.demo.dto.request.review.ReviewUpdateRequestDto;
import com.example.demo.dto.response.review.ReviewResponseDto;
import com.example.demo.entity.Review;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UnauthorizedActionException;

/**
 * 파일명: ReviewServiceTest.java
 * 설명: ReviewService 단위 테스트 (Mockito, 실제 DB 미사용)
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | Review 도메인 신규 생성에 따른 단위 테스트 작성 (Phase 2)
 */
@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewDao reviewDao;

    @InjectMocks
    private ReviewService reviewService;

    private Review existingReview;

    @BeforeEach
    void setUp() {
        existingReview = new Review();
        existingReview.setReviewId(1);
        existingReview.setTreatmentId(10);
        existingReview.setUserId("testuser");
        existingReview.setTitle("좋아요");
        existingReview.setContent("만족스러운 시술이었습니다.");
        existingReview.setHits(5);
        existingReview.setIsHidden(false);
    }

    @Test
    void 리뷰등록_성공() {
        ReviewCreateRequestDto request = new ReviewCreateRequestDto();
        request.setTreatmentId(10);
        request.setUserId("testuser");
        request.setTitle("좋아요");
        request.setContent("만족스러운 시술이었습니다.");

        when(reviewDao.insertReview(any(Review.class))).thenReturn(1);

        int result = reviewService.createReview(request);

        assertThat(result).isEqualTo(1);
        verify(reviewDao).insertReview(any(Review.class));
    }

    @Test
    void 리뷰목록조회_성공() {
        when(reviewDao.selectAllReviews()).thenReturn(List.of(existingReview));

        List<ReviewResponseDto> result = reviewService.getAllReviews();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getReviewId()).isEqualTo(1);
    }

    @Test
    void 리뷰상세조회_성공시_조회수가_증가한다() {
        when(reviewDao.selectReviewById(1)).thenReturn(existingReview);

        ReviewResponseDto response = reviewService.getReviewById(1);

        assertThat(response.getHits()).isEqualTo(6);
        verify(reviewDao, times(1)).incrementHits(1);
    }

    @Test
    void 리뷰상세조회_존재하지않으면_ResourceNotFoundException() {
        when(reviewDao.selectReviewById(anyInt())).thenReturn(null);

        assertThatThrownBy(() -> reviewService.getReviewById(999))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(reviewDao, never()).incrementHits(anyInt());
    }

    @Test
    void 리뷰수정_작성자본인이아니면_UnauthorizedActionException() {
        ReviewUpdateRequestDto request = new ReviewUpdateRequestDto();
        request.setReviewId(1);
        request.setUserId("otheruser");
        request.setTitle("수정된 제목");
        request.setContent("수정된 내용");

        when(reviewDao.selectReviewById(1)).thenReturn(existingReview);

        assertThatThrownBy(() -> reviewService.modifyReview(request))
                .isInstanceOf(UnauthorizedActionException.class);
    }

    @Test
    void 리뷰수정_존재하지않으면_ResourceNotFoundException() {
        ReviewUpdateRequestDto request = new ReviewUpdateRequestDto();
        request.setReviewId(999);
        request.setUserId("testuser");
        request.setTitle("수정된 제목");
        request.setContent("수정된 내용");

        when(reviewDao.selectReviewById(999)).thenReturn(null);

        assertThatThrownBy(() -> reviewService.modifyReview(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void 리뷰수정_작성자본인이면_성공() {
        ReviewUpdateRequestDto request = new ReviewUpdateRequestDto();
        request.setReviewId(1);
        request.setUserId("testuser");
        request.setTitle("수정된 제목");
        request.setContent("수정된 내용");

        when(reviewDao.selectReviewById(1)).thenReturn(existingReview);
        when(reviewDao.updateReview(any(Review.class))).thenReturn(1);

        int result = reviewService.modifyReview(request);

        assertThat(result).isEqualTo(1);
    }

    @Test
    void 리뷰삭제_작성자본인이아니면_UnauthorizedActionException() {
        when(reviewDao.selectReviewById(1)).thenReturn(existingReview);

        assertThatThrownBy(() -> reviewService.removeReview(1, "otheruser"))
                .isInstanceOf(UnauthorizedActionException.class);
    }

    @Test
    void 리뷰삭제_작성자본인이면_성공() {
        when(reviewDao.selectReviewById(1)).thenReturn(existingReview);
        when(reviewDao.deleteReview(1)).thenReturn(1);

        int result = reviewService.removeReview(1, "testuser");

        assertThat(result).isEqualTo(1);
    }
}
