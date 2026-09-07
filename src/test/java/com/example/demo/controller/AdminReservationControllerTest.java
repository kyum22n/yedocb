package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.AbstractIntegrationTest;
import com.example.demo.dto.request.reservation.AdminReservationStatusUpdateRequestDto;
import com.example.demo.dto.response.reservation.AdminReservationResponseDto;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.AdminReservationService;

import tools.jackson.databind.ObjectMapper;

/**
 * 파일명: AdminReservationControllerTest.java
 * 설명: 관리자 예약(AdminReservationController) MockMvc 테스트 — 인가, 상태별 조회, 상태 수정을 검증한다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성
 */
class AdminReservationControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private AdminReservationService reservationService;

    private String adminToken() {
        return "Bearer " + jwtTokenProvider.createToken("admin01", List.of("ADMIN"));
    }

    @Test
    void 인증없이_예약목록조회하면_401() throws Exception {
        mockMvc.perform(get("/admin/reservations/all"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void ADMIN권한으로_상태별예약조회하면_200() throws Exception {
        AdminReservationResponseDto dto = new AdminReservationResponseDto();
        dto.setReservationId(1);
        dto.setUId("user01");
        dto.setReservationStatus("PENDING");
        when(reservationService.getReservationsByStatus("PENDING")).thenReturn(List.of(dto));

        mockMvc.perform(get("/admin/reservations/status")
                        .param("reservationStatus", "PENDING")
                        .header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].reservationStatus").value("PENDING"));
    }

    @Test
    void 예약상태수정_성공하면_200() throws Exception {
        AdminReservationStatusUpdateRequestDto request = new AdminReservationStatusUpdateRequestDto();
        request.setReservationId(1);
        request.setReservationStatus("CONFIRMED");

        when(reservationService.modifyReservationStatus(any())).thenReturn(1);

        mockMvc.perform(put("/admin/reservations/status/update")
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));
    }

    @Test
    void 예약삭제_성공하면_200() throws Exception {
        when(reservationService.removeReservation(4)).thenReturn(1);

        mockMvc.perform(delete("/admin/reservations/delete/4").header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));
    }
}
