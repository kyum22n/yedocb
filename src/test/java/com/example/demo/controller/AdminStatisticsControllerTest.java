package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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
import com.example.demo.dto.request.statistics.StatisticsSearchRequestDto;
import com.example.demo.dto.response.statistics.ReservationStatisticsResponseDto;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.AdminStatisticsService;

import tools.jackson.databind.ObjectMapper;

/**
 * 파일명: AdminStatisticsControllerTest.java
 * 설명: 관리자 통계(AdminStatisticsController) MockMvc 테스트 — 인가 규칙과 예약 통계 조회를 검증한다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성
 */
class AdminStatisticsControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private AdminStatisticsService statisticsService;

    private String adminToken() {
        return "Bearer " + jwtTokenProvider.createToken("admin01", List.of("ADMIN"));
    }

    @Test
    void 인증없이_통계조회하면_401() throws Exception {
        mockMvc.perform(post("/admin/statistics/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void ADMIN권한으로_예약통계조회하면_200() throws Exception {
        ReservationStatisticsResponseDto dto = new ReservationStatisticsResponseDto();
        dto.setTotalReservationCount(100);
        dto.setNoShowRate(0.05);
        when(statisticsService.getReservationStatistics(any())).thenReturn(dto);

        StatisticsSearchRequestDto request = new StatisticsSearchRequestDto();
        request.setStartDate(java.time.LocalDate.of(2026, 1, 1));
        request.setEndDate(java.time.LocalDate.of(2026, 12, 31));

        mockMvc.perform(post("/admin/statistics/reservations")
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalReservationCount").value(100));
    }

    @Test
    void 인기진료항목통계조회하면_200() throws Exception {
        when(statisticsService.getTreatmentStatistics(any())).thenReturn(List.of());

        mockMvc.perform(post("/admin/statistics/treatments")
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
