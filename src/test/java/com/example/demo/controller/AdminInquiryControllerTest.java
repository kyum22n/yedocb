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
import com.example.demo.dto.request.inquiry.AdminInquiryAnswerCreateRequestDto;
import com.example.demo.dto.response.inquiry.AdminInquiryResponseDto;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.AdminInquiryService;

import tools.jackson.databind.ObjectMapper;

/**
 * 파일명: AdminInquiryControllerTest.java
 * 설명: 관리자 문의(AdminInquiryController) MockMvc 테스트 — 인가 규칙, 목록/답변등록 응답을 검증한다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성
 */
class AdminInquiryControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private AdminInquiryService inquiryService;

    private String adminToken() {
        return "Bearer " + jwtTokenProvider.createToken("admin01", List.of("ADMIN"));
    }

    @Test
    void 인증없이_문의목록조회하면_401() throws Exception {
        mockMvc.perform(get("/admin/inquiries/all"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void ADMIN권한으로_문의목록조회하면_200() throws Exception {
        AdminInquiryResponseDto dto = new AdminInquiryResponseDto();
        dto.setInquiryId(1);
        dto.setUId("user01");
        dto.setInquiryStatus("WAITING");
        when(inquiryService.getAllInquiries()).thenReturn(List.of(dto));

        mockMvc.perform(get("/admin/inquiries/all").header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].inquiryId").value(1));
    }

    @Test
    void 답변등록_성공하면_200() throws Exception {
        AdminInquiryAnswerCreateRequestDto request = new AdminInquiryAnswerCreateRequestDto();
        request.setInquiryId(1);
        request.setAdminId(1);
        request.setAnswerContent("답변 내용입니다.");

        when(inquiryService.createInquiryAnswer(any())).thenReturn(1);

        mockMvc.perform(post("/admin/inquiries/answers/register")
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));
    }

    @Test
    void 문의삭제_성공하면_200() throws Exception {
        when(inquiryService.removeInquiry(7)).thenReturn(1);

        mockMvc.perform(delete("/admin/inquiries/delete/7").header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));
    }
}
