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
 * 설명: 사용자용 공지/이벤트(NoticeController) MockMvc 테스트. "/notices"는 SecurityPaths의
 *       permitAll 화이트리스트에 없으므로 anyRequest().authenticated() 규칙이 적용되어
 *       인증 없이는 조회할 수 없음을 확인한다(공개 게시판이 아니라 로그인 사용자 전용임을
 *       이번 테스트 작성 과정에서 재확인 — 프론트가 비로그인 사용자에게도 공지를 보여준다면
 *       배포 전 SecurityPaths.PUBLIC_GET_PATTERNS에 "/notices/**" 추가 여부를 사용자와 확인해야 한다).
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성
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
    void 인증없이_공지목록조회하면_401() throws Exception {
        mockMvc.perform(get("/notices/all"))
                .andExpect(status().isUnauthorized());
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
