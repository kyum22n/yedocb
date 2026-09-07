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
import com.example.demo.dto.response.notice.NoticeResponseDto;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.NoticeService;

/**
 * 파일명: NoticeControllerTest.java
 * 설명: 사용자용 공지/이벤트(NoticeController) MockMvc 테스트.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성 — 당시 "/notices"가 permitAll 화이트리스트에
 *                        없어 비로그인 조회가 401이었음(발견된 이슈로 기록, docs/test-report.md)
 * 2026-09-07 | 배포 후 디버깅 | Phase 11 실사용 테스트에서 메인 페이지 공지 팝업이 비로그인
 *                        사용자에게 401로 안 보이는 게 확인되어 SecurityPaths.PUBLIC_GET_PATTERNS에
 *                        "/notices/**" 추가. 아래 테스트를 401 → 200 기대로 변경
 */
class NoticeControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private NoticeService noticeService;

    private String userToken() {
        return "Bearer " + jwtTokenProvider.createToken("user01", List.of("USER"));
    }

    @Test
    void 인증없이_공지목록조회해도_200() throws Exception {
        NoticeResponseDto dto = new NoticeResponseDto();
        dto.setNoticeId(1);
        dto.setTitle("공지 제목");
        when(noticeService.getVisibleNotices()).thenReturn(List.of(dto));

        mockMvc.perform(get("/notices/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("공지 제목"));
    }

    @Test
    void 인증된사용자가_공지목록조회하면_200() throws Exception {
        NoticeResponseDto dto = new NoticeResponseDto();
        dto.setNoticeId(1);
        dto.setTitle("공지 제목");
        when(noticeService.getVisibleNotices()).thenReturn(List.of(dto));

        mockMvc.perform(get("/notices/all").header("Authorization", userToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("공지 제목"));
    }
}
