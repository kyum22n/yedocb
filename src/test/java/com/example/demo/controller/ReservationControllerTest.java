package com.example.demo.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.AbstractIntegrationTest;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.ReservationService;

/**
 * 파일명: ReservationControllerTest.java
 * 설명: 사용자용 예약(ReservationController) MockMvc 테스트. disabled-times는 SecurityPaths의
 *       PUBLIC_GET_PATTERNS에 있어 인증 없이도 조회 가능하고, 나머지는 인증이 필요하다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성
 */
class ReservationControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private ReservationService reservationService;

    private String userToken() {
        return "Bearer " + jwtTokenProvider.createToken("user01", List.of("USER"));
    }

    @Test
    void 마감시간대조회는_인증없이도_200() throws Exception {
        when(reservationService.getDisabledTimes(LocalDate.of(2026, 9, 10)))
                .thenReturn(List.of(LocalTime.of(10, 0)));

        mockMvc.perform(get("/reservations/disabled-times").param("reservationDate", "2026-09-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("10:00:00"));
    }

    @Test
    void 인증없이_예약등록하면_401() throws Exception {
        mockMvc.perform(post("/reservations/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 회원별예약목록조회는_인증필요() throws Exception {
        when(reservationService.getReservationsByUId("user01")).thenReturn(List.of());

        mockMvc.perform(get("/reservations/member")
                        .param("uId", "user01")
                        .header("Authorization", userToken()))
                .andExpect(status().isOk());
    }
}
