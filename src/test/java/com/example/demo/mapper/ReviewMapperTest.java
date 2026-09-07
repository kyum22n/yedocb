package com.example.demo.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.AbstractIntegrationTest;
import com.example.demo.dao.ReviewDao;
import com.example.demo.dao.UserDao;
import com.example.demo.entity.Review;
import com.example.demo.entity.User;

/**
 * 파일명: ReviewMapperTest.java
 * 설명: ReviewMapper.xml을 실제 Postgres 대상으로 검증한다. 등록 시 hits/is_hidden 기본값,
 *       숨김 리뷰가 사용자 조회(selectAllReviews)에서 제외되고 관리자 조회
 *       (selectAllReviewsForAdmin)에는 포함되는지, 조회수 증가가 반영되는지 확인한다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성
 */
class ReviewMapperTest extends AbstractIntegrationTest {

    @Autowired
    private ReviewDao reviewDao;

    @Autowired
    private UserDao userDao;

    private String createUser(String id) {
        User user = new User();
        user.setUId(id);
        user.setUPwd("pwd");
        user.setUEmail(id + "@example.com");
        user.setUName("사용자");
        userDao.insertUser(user);
        return id;
    }

    private Review newReview(String userId, String title) {
        Review review = new Review();
        review.setUserId(userId);
        review.setTitle(title);
        review.setContent("내용");
        return review;
    }

    @Test
    void 리뷰등록하면_생성된PK와_기본값이_채워진다() {
        String uId = createUser("reviewuser1");
        Review review = newReview(uId, "첫 리뷰");

        reviewDao.insertReview(review);

        assertThat(review.getReviewId()).isNotNull();
        Review found = reviewDao.selectReviewById(review.getReviewId());
        assertThat(found.getHits()).isEqualTo(0);
        assertThat(found.getIsHidden()).isFalse();
    }

    @Test
    void 숨김리뷰는_사용자목록에서_제외되고_관리자목록에는_포함된다() {
        String uId = createUser("reviewuser2");
        Review review = newReview(uId, "숨겨질 리뷰");
        reviewDao.insertReview(review);
        reviewDao.updateReviewHidden(review.getReviewId(), true);

        List<Review> userVisible = reviewDao.selectAllReviews();
        List<Review> adminVisible = reviewDao.selectAllReviewsForAdmin();

        assertThat(userVisible).extracting(Review::getReviewId).doesNotContain(review.getReviewId());
        assertThat(adminVisible).extracting(Review::getReviewId).contains(review.getReviewId());
    }

    @Test
    void 조회수증가가_반영된다() {
        String uId = createUser("reviewuser3");
        Review review = newReview(uId, "조회수 테스트");
        reviewDao.insertReview(review);

        reviewDao.incrementHits(review.getReviewId());
        reviewDao.incrementHits(review.getReviewId());

        assertThat(reviewDao.selectReviewById(review.getReviewId()).getHits()).isEqualTo(2);
    }

    @Test
    void 존재하지않는_사용자로_등록하면_FK제약에_의해_예외가발생한다() {
        Review review = newReview("no-such-user", "실패할 리뷰");

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> reviewDao.insertReview(review))
                .isInstanceOf(org.springframework.dao.DataIntegrityViolationException.class);
    }
}
