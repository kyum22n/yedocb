package com.example.demo.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.AbstractIntegrationTest;
import com.example.demo.dto.response.dashboard.AdminDashboardResponseDto;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.AdminDashboardService;

/**
 * 파일명: AdminDashboardControllerTest.java
 * 설명: 관리자 대시보드(AdminDashboardController) MockMvc 테스트 — 인가 규칙과 정상 응답을 검증한다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성
 */
class AdminDashboardControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private AdminDashboardService dashboardService;

    private String adminToken() {
        return "Bearer " + jwtTokenProvider.createToken("admin01", List.of("ADMIN"));
    }

    @Test
    void 인증없이_대시보드조회하면_401() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void ADMIN권한으로_대시보드조회하면_200() throws Exception {
        AdminDashboardResponseDto response = new AdminDashboardResponseDto();
        response.setTodayReservationCount(5);
        response.setPendingReservationCount(2);
        response.setTodayConsultationCount(1);
        response.setWaitingInquiryCount(0);
        response.setPmsFailedCount(0);
        response.setRecentReservations(List.of());
        response.setRecentInquiries(List.of());
        when(dashboardService.getDashboard()).thenReturn(response);

        mockMvc.perform(get("/admin/dashboard").header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.todayReservationCount").value(5));
    }
}
