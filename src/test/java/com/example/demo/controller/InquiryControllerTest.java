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
import com.example.demo.dto.request.inquiry.InquiryCreateRequestDto;
import com.example.demo.dto.response.inquiry.InquiryResponseDto;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.InquiryService;

import tools.jackson.databind.ObjectMapper;

/**
 * 파일명: InquiryControllerTest.java
 * 설명: 사용자용 문의(InquiryController) MockMvc 테스트 — 인증 필요 여부와 등록/조회를 검증한다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성
 */
class InquiryControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private InquiryService inquiryService;

    private String userToken() {
        return "Bearer " + jwtTokenProvider.createToken("user01", List.of("USER"));
    }

    @Test
    void 인증없이_문의등록하면_401() throws Exception {
        mockMvc.perform(post("/inquiries/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 인증된사용자가_문의등록하면_200() throws Exception {
        InquiryCreateRequestDto request = new InquiryCreateRequestDto();
        request.setUId("user01");
        request.setInquiryType("GENERAL");
        request.setTitle("문의 제목");
        request.setContent("문의 내용");

        when(inquiryService.createInquiry(any())).thenReturn(1);

        mockMvc.perform(post("/inquiries/register")
                        .header("Authorization", userToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));
    }

    @Test
    void 회원별문의목록조회하면_200() throws Exception {
        InquiryResponseDto dto = new InquiryResponseDto();
        dto.setInquiryId(1);
        dto.setUId("user01");
        when(inquiryService.getInquiriesByUId("user01")).thenReturn(List.of(dto));

        mockMvc.perform(get("/inquiries/member")
                        .param("uId", "user01")
                        .header("Authorization", userToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].inquiryId").value(1));
    }
}
