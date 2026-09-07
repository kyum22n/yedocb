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
import com.example.demo.dto.request.notice.AdminNoticeCreateRequestDto;
import com.example.demo.dto.response.notice.AdminNoticeResponseDto;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.AdminNoticeService;

import tools.jackson.databind.ObjectMapper;

/**
 * 파일명: AdminNoticeControllerTest.java
 * 설명: 관리자 공지/이벤트(AdminNoticeController) MockMvc 테스트.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성
 */
class AdminNoticeControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private AdminNoticeService noticeService;

    private String adminToken() {
        return "Bearer " + jwtTokenProvider.createToken("admin01", List.of("ADMIN"));
    }

    @Test
    void 인증없이_공지목록조회하면_401() throws Exception {
        mockMvc.perform(get("/admin/notices/all"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void ADMIN권한으로_공지목록조회하면_200() throws Exception {
        AdminNoticeResponseDto dto = new AdminNoticeResponseDto();
        dto.setNoticeId(1);
        dto.setTitle("공지 제목");
        when(noticeService.getAllNotices()).thenReturn(List.of(dto));

        mockMvc.perform(get("/admin/notices/all").header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("공지 제목"));
    }

    @Test
    void 공지등록_성공하면_200() throws Exception {
        AdminNoticeCreateRequestDto request = new AdminNoticeCreateRequestDto();
        request.setTitle("새 공지");
        request.setContent("내용");
        request.setNoticeType("NOTICE");
        request.setIsVisible(true);

        when(noticeService.createNotice(any())).thenReturn(1);

        mockMvc.perform(post("/admin/notices/register")
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));
    }

    @Test
    void 공지삭제_성공하면_200() throws Exception {
        when(noticeService.removeNotice(9)).thenReturn(1);

        mockMvc.perform(delete("/admin/notices/delete/9").header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));
    }
}
