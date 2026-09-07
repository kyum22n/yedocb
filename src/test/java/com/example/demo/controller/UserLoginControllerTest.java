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
import com.example.demo.dao.UserDao;
import com.example.demo.dto.request.auth.UserLoginRequestDto;
import com.example.demo.entity.User;
import com.example.demo.security.JwtTokenProvider;

import tools.jackson.databind.ObjectMapper;

/**
 * 파일명: UserLoginControllerTest.java
 * 설명: 사용자 로그인/토큰재발급(UserLoginController) MockMvc 테스트 — permitAll 경로, 로그인 성공/실패,
 *       리프레시 토큰 재발급을 검증한다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성
 */
class UserLoginControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserDao userDao;

    @Test
    void 로그인성공하면_200과_액세스_리프레시토큰반환() throws Exception {
        User user = new User();
        user.setUId("user01");
        user.setUPwd(passwordEncoder.encode("Password1!"));
        when(userDao.selectUserById("user01")).thenReturn(user);

        UserLoginRequestDto request = new UserLoginRequestDto();
        request.setUId("user01");
        request.setUPwd("Password1!");

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user01"))
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }

    @Test
    void 존재하지않는아이디면_401() throws Exception {
        when(userDao.selectUserById("nouser")).thenReturn(null);

        UserLoginRequestDto request = new UserLoginRequestDto();
        request.setUId("nouser");
        request.setUPwd("Password1!");

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 잘못된리프레시토큰이면_401() throws Exception {
        mockMvc.perform(post("/api/user/refresh").param("refreshToken", "not-a-real-token"))
                .andExpect(status().isUnauthorized());
    }
}
