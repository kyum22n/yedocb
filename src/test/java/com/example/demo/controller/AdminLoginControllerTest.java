package com.example.demo.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.AbstractIntegrationTest;
import com.example.demo.dao.AdminDao;
import com.example.demo.dto.request.auth.AdminLoginRequestDto;
import com.example.demo.entity.Admin;

import tools.jackson.databind.ObjectMapper;

/**
 * 파일명: AdminLoginControllerTest.java
 * 설명: 관리자 로그인(AdminLoginController) MockMvc 테스트 — permitAll 경로 확인, 로그인 성공 시
 *       adminId/adminRole이 응답에 포함되는지, 비밀번호 불일치 시 401을 확인한다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성
 */
class AdminLoginControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private AdminDao adminDao;

    @Test
    void 로그인성공하면_200과_토큰_adminId_adminRole반환() throws Exception {
        Admin admin = new Admin();
        admin.setAdminId(1);
        admin.setAdminLoginId("admin01");
        admin.setAdminPassword(passwordEncoder.encode("Password1!"));
        admin.setAdminRole("ADMIN");
        when(adminDao.selectAdminByLoginId("admin01")).thenReturn(admin);

        AdminLoginRequestDto request = new AdminLoginRequestDto();
        request.setAdminLoginId("admin01");
        request.setAdminPassword("Password1!");

        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.adminId").value(1))
                .andExpect(jsonPath("$.adminRole").value("ADMIN"))
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    void 비밀번호틀리면_401() throws Exception {
        Admin admin = new Admin();
        admin.setAdminId(1);
        admin.setAdminLoginId("admin01");
        admin.setAdminPassword(passwordEncoder.encode("Password1!"));
        admin.setAdminRole("ADMIN");
        when(adminDao.selectAdminByLoginId("admin01")).thenReturn(admin);

        AdminLoginRequestDto request = new AdminLoginRequestDto();
        request.setAdminLoginId("admin01");
        request.setAdminPassword("wrong-password");

        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 아이디없으면_400() throws Exception {
        AdminLoginRequestDto request = new AdminLoginRequestDto();
        request.setAdminPassword("Password1!");
        // adminLoginId 누락

        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
