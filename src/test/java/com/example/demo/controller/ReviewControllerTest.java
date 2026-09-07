package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.AbstractIntegrationTest;
import com.example.demo.dto.request.review.ReviewCreateRequestDto;
import com.example.demo.dto.response.review.ReviewResponseDto;
import com.example.demo.entity.Review;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.ReviewService;

import tools.jackson.databind.ObjectMapper;

/**
 * 파일명: ReviewControllerTest.java
 * 설명: 사용자용 리뷰(ReviewController) MockMvc 테스트. 목록/상세 GET은 permitAll(SecurityPaths의
 *       PUBLIC_GET_PATTERNS "/reviews/**")이고, 등록/수정/삭제는 인증이 필요하다. 작성자 식별이
 *       요청 바디가 아니라 JWT 인증 주체(Authentication.getName())에서 오는지도 확인한다
 *       (알려진 이슈 수정 — 다른 사용자 명의로 리뷰 작성 방지).
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성
 */
class ReviewControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private ReviewService reviewService;

    private String userToken() {
        return "Bearer " + jwtTokenProvider.createToken("user01", List.of("USER"));
    }

    @Test
    void 리뷰목록조회는_인증없이도_200() throws Exception {
        Review review = new Review();
        review.setReviewId(1);
        review.setUserId("user01");
        review.setTitle("후기");
        review.setContent("내용");
        when(reviewService.getAllReviews()).thenReturn(List.of(ReviewResponseDto.from(review)));

        mockMvc.perform(get("/reviews/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("후기"));
    }

    @Test
    void 인증없이_리뷰등록하면_401() throws Exception {
        mockMvc.perform(post("/reviews/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 인증된사용자가_리뷰등록하면_작성자는_토큰의사용자ID이다() throws Exception {
        ReviewCreateRequestDto request = new ReviewCreateRequestDto();
        request.setTreatmentId(1);
        request.setTitle("좋아요");
        request.setContent("만족스러운 시술이었습니다.");

        when(reviewService.createReview(any(), eq("user01"))).thenReturn(1);

        mockMvc.perform(post("/reviews/register")
                        .header("Authorization", userToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));
    }

    @Test
    void 리뷰등록_필수값누락시_400() throws Exception {
        ReviewCreateRequestDto request = new ReviewCreateRequestDto();
        // treatmentId, title, content 누락

        mockMvc.perform(post("/reviews/register")
                        .header("Authorization", userToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 인증없이_리뷰삭제하면_401() throws Exception {
        mockMvc.perform(delete("/reviews/delete").param("reviewId", "1"))
                .andExpect(status().isUnauthorized());
    }
}
