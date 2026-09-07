package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import com.example.demo.dto.request.consultation.AdminConsultationConvertRequestDto;
import com.example.demo.dto.request.consultation.AdminConsultationCreateRequestDto;
import com.example.demo.dto.response.consultation.AdminConsultationResponseDto;
import com.example.demo.entity.Consultation;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.AdminConsultationService;

import tools.jackson.databind.ObjectMapper;

/**
 * 파일명: AdminConsultationControllerTest.java
 * 설명: 관리자 상담 controller MockMvc 테스트. "/admin/**"는 ADMIN/SUPERADMIN 권한이
 *       필요하다는 인가 규칙과, 상담 예약 전환(convert) 성공/검증실패 응답을 검증한다.
 *       실제 SecurityFilterChain + 실제 DB(Testcontainers)로 뜬 전체 컨텍스트 위에서 실행하고
 *       서비스 계층만 목(mock)으로 대체한다(AbstractIntegrationTest 참고 — 순수 @WebMvcTest
 *       슬라이스에서는 유효한 JWT를 보내도 401이 발생하는 프레임워크 이슈가 있었다).
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성
 */
class AdminConsultationControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private AdminConsultationService consultationService;

    private String adminToken() {
        return "Bearer " + jwtTokenProvider.createToken("admin01", List.of("ADMIN"));
    }

    private String userToken() {
        return "Bearer " + jwtTokenProvider.createToken("user01", List.of("USER"));
    }

    @Test
    void 인증토큰없이_상담목록조회하면_401() throws Exception {
        mockMvc.perform(get("/admin/consultations/all"))
                .andExpect(status().isUnauthorized());
    }

    // 참고(docs/test-report.md 트러블슈팅 항목): 동일한 "권한부족" 시나리오가 MockMvc
    // 환경에서는 403으로, 실제 내장 톰캣(RealServerJwtSmokeTest)에서는 401로 응답하는
    // 차이를 발견했다 — MockMvc가 표준적인 AccessDeniedHandler 흐름을 그대로 타는 반면
    // 실제 서버는 다른 경로로 응답 상태를 결정하는 것으로 보인다(SecurityConfig에 커스텀
    // AccessDeniedHandler가 없음). 프론트엔드가 401/403을 다르게 처리한다면 재확인이 필요하다.
    @Test
    void USER권한으로_상담목록조회하면_403() throws Exception {
        mockMvc.perform(get("/admin/consultations/all")
                        .header("Authorization", userToken()))
                .andExpect(status().isForbidden());
    }

    @Test
    void ADMIN권한으로_상담목록조회하면_200과_목록반환() throws Exception {
        Consultation consultation = new Consultation();
        consultation.setConsultationId(1);
        consultation.setUId("user01");
        consultation.setConsultationStatus("RECEIVED");
        when(consultationService.getAllConsultations())
                .thenReturn(List.of(AdminConsultationResponseDto.from(consultation)));

        mockMvc.perform(get("/admin/consultations/all")
                        .header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].consultationId").value(1))
                .andExpect(jsonPath("$[0].uId").value("user01"));
    }

    @Test
    void 상담예약전환_성공하면_200과_영향행수반환() throws Exception {
        AdminConsultationConvertRequestDto request = new AdminConsultationConvertRequestDto();
        request.setConsultationId(10);
        request.setReservationId(20);
        request.setAdminId(1);

        when(consultationService.convertConsultationToReservation(any())).thenReturn(1);

        mockMvc.perform(put("/admin/consultations/convert")
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));
    }

    @Test
    void 상담예약전환_reservationId누락시_400() throws Exception {
        AdminConsultationConvertRequestDto request = new AdminConsultationConvertRequestDto();
        request.setConsultationId(10);
        // reservationId 누락

        mockMvc.perform(put("/admin/consultations/convert")
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 상담예약전환_존재하지않는상담이면_404() throws Exception {
        AdminConsultationConvertRequestDto request = new AdminConsultationConvertRequestDto();
        request.setConsultationId(999);
        request.setReservationId(20);

        when(consultationService.convertConsultationToReservation(any()))
                .thenThrow(new ResourceNotFoundException("존재하지 않는 상담입니다."));

        mockMvc.perform(put("/admin/consultations/convert")
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("존재하지 않는 상담입니다."));
    }

    @Test
    void 상담삭제_성공하면_200() throws Exception {
        when(consultationService.removeConsultation(5)).thenReturn(1);

        mockMvc.perform(delete("/admin/consultations/delete/5")
                        .header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));
    }

    @Test
    void 상담등록_uId누락시_400() throws Exception {
        AdminConsultationCreateRequestDto request = new AdminConsultationCreateRequestDto();
        // uId 누락

        mockMvc.perform(post("/admin/consultations/register")
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
