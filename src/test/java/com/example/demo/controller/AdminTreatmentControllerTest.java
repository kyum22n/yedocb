package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
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
import com.example.demo.dto.request.treatment.TreatmentCreateRequestDto;
import com.example.demo.dto.response.treatment.AdminTreatmentResponseDto;
import com.example.demo.entity.Treatment;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.AdminTreatmentService;

import tools.jackson.databind.ObjectMapper;

/**
 * 파일명: AdminTreatmentControllerTest.java
 * 설명: 관리자 진료항목(AdminTreatmentController) MockMvc 테스트.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성
 */
class AdminTreatmentControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private AdminTreatmentService treatmentService;

    private String adminToken() {
        return "Bearer " + jwtTokenProvider.createToken("admin01", List.of("ADMIN"));
    }

    @Test
    void 인증없이_진료항목목록조회하면_401() throws Exception {
        mockMvc.perform(get("/admin/treatments/all"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void ADMIN권한으로_진료항목목록조회하면_200() throws Exception {
        Treatment treatment = new Treatment();
        treatment.setTreatmentId(1);
        treatment.setCategoryId(1);
        treatment.setTreatmentName("보톡스");
        treatment.setIsVisible(true);
        when(treatmentService.getAllTreatments()).thenReturn(List.of(AdminTreatmentResponseDto.from(treatment)));

        mockMvc.perform(get("/admin/treatments/all").header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].treatmentName").value("보톡스"));
    }

    @Test
    void 진료항목등록_필수값누락시_400() throws Exception {
        TreatmentCreateRequestDto request = new TreatmentCreateRequestDto();
        // categoryId, treatmentName 누락

        mockMvc.perform(post("/admin/treatments/register")
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 진료항목등록_성공하면_200() throws Exception {
        TreatmentCreateRequestDto request = new TreatmentCreateRequestDto();
        request.setCategoryId(1);
        request.setTreatmentName("필러");
        request.setIsVisible(true);

        when(treatmentService.createTreatment(any())).thenReturn(1);

        mockMvc.perform(post("/admin/treatments/register")
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));
    }

    @Test
    void 진료항목삭제_성공하면_200() throws Exception {
        when(treatmentService.deleteTreatment(2)).thenReturn(1);

        mockMvc.perform(delete("/admin/treatments/delete/2").header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));
    }
}
