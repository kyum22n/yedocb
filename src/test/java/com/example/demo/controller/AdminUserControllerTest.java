package com.example.demo.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.AbstractIntegrationTest;
import com.example.demo.dto.response.user.AdminUserListResponseDto;
import com.example.demo.entity.User;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.AdminUserService;

/**
 * 파일명: AdminUserControllerTest.java
 * 설명: 관리자 회원관리(AdminUserController) MockMvc 테스트 — 인가 규칙과, 목록 응답에 비밀번호
 *       필드가 없는지(알려진 이슈 수정 확인) 검증한다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성
 */
class AdminUserControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private AdminUserService adminUserService;

    private String adminToken() {
        return "Bearer " + jwtTokenProvider.createToken("admin01", List.of("ADMIN"));
    }

    @Test
    void 인증없이_회원목록조회하면_401() throws Exception {
        mockMvc.perform(get("/api/admin/user/list"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void ADMIN권한으로_회원목록조회하면_200이고_비밀번호필드가_없다() throws Exception {
        User user = new User();
        user.setUId("user01");
        user.setUPwd("secret-hash");
        user.setUName("홍길동");
        user.setUEmail("user01@example.com");
        when(adminUserService.getAllUsers()).thenReturn(List.of(AdminUserListResponseDto.from(user)));

        mockMvc.perform(get("/api/admin/user/list").header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].uId").value("user01"))
                .andExpect(jsonPath("$[0].uPwd").doesNotExist());
    }

    @Test
    void 회원삭제_성공하면_200() throws Exception {
        when(adminUserService.removeAdminUser("user01")).thenReturn(1);

        mockMvc.perform(delete("/api/admin/user/delete")
                        .param("uId", "user01")
                        .header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));
    }
}
