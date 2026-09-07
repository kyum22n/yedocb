package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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
import com.example.demo.dto.request.consultation.ConsultationCreateRequestDto;
import com.example.demo.dto.response.consultation.ConsultationResponseDto;
import com.example.demo.entity.Consultation;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.ConsultationService;

import tools.jackson.databind.ObjectMapper;

/**
 * 파일명: ConsultationControllerTest.java
 * 설명: 사용자용 상담(ConsultationController) MockMvc 테스트. "/consultations"는 permitAll/역할별
 *       화이트리스트 어디에도 없어 anyRequest().authenticated() 규칙이 적용된다 — 인증만 되면
 *       어떤 역할이든 접근 가능함을 확인한다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성
 */
class ConsultationControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private ConsultationService consultationService;

    private String userToken() {
        return "Bearer " + jwtTokenProvider.createToken("user01", List.of("USER"));
    }

    @Test
    void 인증없이_상담등록하면_401() throws Exception {
        mockMvc.perform(post("/consultations/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 상담등록_필수값누락시_400() throws Exception {
        ConsultationCreateRequestDto request = new ConsultationCreateRequestDto();
        // uId, treatmentId 누락

        mockMvc.perform(post("/consultations/register")
                        .header("Authorization", userToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 인증된사용자가_상담등록하면_200() throws Exception {
        ConsultationCreateRequestDto request = new ConsultationCreateRequestDto();
        request.setUId("user01");
        request.setTreatmentId(1);

        when(consultationService.createConsultation(any())).thenReturn(1);

        mockMvc.perform(post("/consultations/register")
                        .header("Authorization", userToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));
    }

    @Test
    void 회원별상담목록조회하면_200() throws Exception {
        Consultation consultation = new Consultation();
        consultation.setConsultationId(1);
        consultation.setUId("user01");
        when(consultationService.getConsultationsByUId("user01"))
                .thenReturn(List.of(ConsultationResponseDto.from(consultation)));

        mockMvc.perform(get("/consultations/member")
                        .param("uId", "user01")
                        .header("Authorization", userToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].consultationId").value(1));
    }
}
