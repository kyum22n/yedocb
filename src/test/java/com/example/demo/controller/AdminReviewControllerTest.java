package com.example.demo.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.AbstractIntegrationTest;
import com.example.demo.dto.response.review.AdminReviewResponseDto;
import com.example.demo.entity.Review;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.AdminReviewService;

/**
 * 파일명: AdminReviewControllerTest.java
 * 설명: 관리자 리뷰 모더레이션(AdminReviewController) MockMvc 테스트 — 인가, 숨김처리, 삭제를 검증한다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성
 */
class AdminReviewControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private AdminReviewService adminReviewService;

    private String adminToken() {
        return "Bearer " + jwtTokenProvider.createToken("admin01", List.of("ADMIN"));
    }

    @Test
    void 인증없이_리뷰목록조회하면_401() throws Exception {
        mockMvc.perform(get("/admin/reviews/all"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void ADMIN권한으로_숨김리뷰포함목록조회하면_200() throws Exception {
        Review review = new Review();
        review.setReviewId(1);
        review.setUserId("user01");
        review.setTitle("후기");
        review.setContent("내용");
        review.setIsHidden(true);
        when(adminReviewService.getAllReviews()).thenReturn(List.of(AdminReviewResponseDto.from(review)));

        mockMvc.perform(get("/admin/reviews/all").header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].isHidden").value(true));
    }

    @Test
    void 리뷰숨김처리_성공하면_200() throws Exception {
        when(adminReviewService.hideReview(1, true)).thenReturn(1);

        mockMvc.perform(put("/admin/reviews/hide")
                        .param("reviewId", "1")
                        .param("isHidden", "true")
                        .header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));
    }

    @Test
    void 리뷰삭제_성공하면_200() throws Exception {
        when(adminReviewService.removeReview(1)).thenReturn(1);

        mockMvc.perform(delete("/admin/reviews/delete")
                        .param("reviewId", "1")
                        .header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));
    }
}
