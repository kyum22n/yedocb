package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.AbstractIntegrationTest;
import com.example.demo.dto.request.schedule.AdminStaffScheduleCreateRequestDto;
import com.example.demo.dto.response.schedule.AdminStaffScheduleResponseDto;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.AdminStaffScheduleService;

import tools.jackson.databind.ObjectMapper;

/**
 * 파일명: AdminStaffScheduleControllerTest.java
 * 설명: 관리자 직원일정(AdminStaffScheduleController) MockMvc 테스트 — 인가 규칙과, 서비스 계층의
 *       동일 관리자/날짜 중복 일정 검사(DuplicateResourceException)가 409로 변환되는지 검증한다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성
 */
class AdminStaffScheduleControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private AdminStaffScheduleService staffScheduleService;

    private String adminToken() {
        return "Bearer " + jwtTokenProvider.createToken("admin01", List.of("ADMIN"));
    }

    @Test
    void 인증없이_일정목록조회하면_401() throws Exception {
        mockMvc.perform(get("/admin/staff-schedules/all"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 날짜별일정조회하면_200() throws Exception {
        AdminStaffScheduleResponseDto dto = new AdminStaffScheduleResponseDto();
        dto.setScheduleId(1);
        dto.setAdminId(1);
        dto.setScheduleDate(LocalDate.of(2026, 9, 10));
        dto.setScheduleType("WORK");
        when(staffScheduleService.getStaffSchedulesByDate(LocalDate.of(2026, 9, 10)))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/admin/staff-schedules/date")
                        .param("scheduleDate", "2026-09-10")
                        .header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].scheduleType").value("WORK"));
    }

    @Test
    void 동일관리자_동일날짜중복등록시_409() throws Exception {
        AdminStaffScheduleCreateRequestDto request = new AdminStaffScheduleCreateRequestDto();
        request.setAdminId(1);
        request.setScheduleDate(LocalDate.of(2026, 9, 10));
        request.setScheduleType("WORK");

        when(staffScheduleService.createStaffSchedule(any()))
                .thenThrow(new DuplicateResourceException("이미 해당 날짜에 등록된 일정이 있습니다."));

        mockMvc.perform(post("/admin/staff-schedules/register")
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void 일정등록_필수값누락시_400() throws Exception {
        AdminStaffScheduleCreateRequestDto request = new AdminStaffScheduleCreateRequestDto();
        // adminId, scheduleDate, scheduleType 모두 누락

        mockMvc.perform(post("/admin/staff-schedules/register")
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 일정삭제_성공하면_200() throws Exception {
        when(staffScheduleService.removeStaffSchedule(2)).thenReturn(1);

        mockMvc.perform(delete("/admin/staff-schedules/delete/2").header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));
    }
}
