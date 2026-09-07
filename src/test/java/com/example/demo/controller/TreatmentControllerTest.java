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
import com.example.demo.dto.response.treatment.TreatmentResponseDto;
import com.example.demo.entity.Treatment;
import com.example.demo.service.TreatmentService;

/**
 * 파일명: TreatmentControllerTest.java
 * 설명: 사용자용 진료항목(TreatmentController) MockMvc 테스트. GET은
 *       SecurityPaths.PUBLIC_GET_PATTERNS("/treatments/**")에 포함되어 인증 없이도 접근 가능하다
 *       — 예약 폼이 예약 전 진료항목을 동적으로 조회할 수 있어야 하기 때문.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성
 */
class TreatmentControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TreatmentService treatmentService;

    @Test
    void 노출진료항목목록조회는_인증없이도_200() throws Exception {
        Treatment treatment = new Treatment();
        treatment.setTreatmentId(1);
        treatment.setTreatmentName("보톡스");
        treatment.setIsReservable(true);
        when(treatmentService.getVisibleTreatments())
                .thenReturn(List.of(TreatmentResponseDto.from(treatment)));

        mockMvc.perform(get("/treatments/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].treatmentName").value("보톡스"));
    }

    @Test
    void 카테고리별진료항목조회는_인증없이도_200() throws Exception {
        when(treatmentService.getVisibleTreatmentsByCategoryId(1)).thenReturn(List.of());

        mockMvc.perform(get("/treatments/category").param("categoryId", "1"))
                .andExpect(status().isOk());
    }
}
