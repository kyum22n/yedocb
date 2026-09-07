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
import com.example.demo.dto.request.admin.AdminCreateRequestDto;
import com.example.demo.dto.response.admin.AdminListResponseDto;
import com.example.demo.entity.Admin;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.AdminService;

import tools.jackson.databind.ObjectMapper;

/**
 * 파일명: AdminControllerTest.java
 * 설명: 관리자 정보(AdminController) MockMvc 테스트 — "/admin/**" 인가, 목록 응답에 비밀번호
 *       해시가 없는지, 등록 검증 실패를 확인한다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성
 */
class AdminControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private AdminService adminService;

    private String adminToken() {
        return "Bearer " + jwtTokenProvider.createToken("admin01", List.of("ADMIN"));
    }

    @Test
    void 인증없이_관리자목록조회하면_401() throws Exception {
        mockMvc.perform(get("/admin/list"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void ADMIN권한으로_관리자목록조회하면_200이고_비밀번호필드가_없다() throws Exception {
        Admin admin = new Admin();
        admin.setAdminId(1);
        admin.setAdminLoginId("admin01");
        admin.setAdminName("관리자");
        when(adminService.getAllAdmins()).thenReturn(List.of(AdminListResponseDto.from(admin)));

        mockMvc.perform(get("/admin/list").header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].adminLoginId").value("admin01"));
    }

    @Test
    void 관리자등록_이메일형식오류시_400() throws Exception {
        AdminCreateRequestDto request = new AdminCreateRequestDto();
        request.setAdminLoginId("newadmin");
        request.setAdminPassword("pw12345!");
        request.setAdminName("새관리자");
        request.setAdminEmail("invalid-email");

        mockMvc.perform(post("/admin/register")
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 관리자등록_성공하면_200() throws Exception {
        AdminCreateRequestDto request = new AdminCreateRequestDto();
        request.setAdminLoginId("newadmin");
        request.setAdminPassword("pw12345!");
        request.setAdminName("새관리자");
        request.setAdminEmail("new@example.com");

        when(adminService.createAdmin(any())).thenReturn(1);

        mockMvc.perform(post("/admin/register")
                        .header("Authorization", adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));
    }

    @Test
    void 관리자삭제_성공하면_200() throws Exception {
        when(adminService.removeAdmin(3)).thenReturn(1);

        mockMvc.perform(delete("/admin/delete/3").header("Authorization", adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));
    }
}
