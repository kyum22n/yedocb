package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import com.example.demo.dto.request.user.UserCreateRequestDto;
import com.example.demo.dto.request.user.UserPasswordUpdateRequestDto;
import com.example.demo.dto.response.user.UserMypageResponseDto;
import com.example.demo.entity.User;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.UserService;

import tools.jackson.databind.ObjectMapper;

/**
 * 파일명: UserControllerTest.java
 * 설명: 회원 정보(UserController) MockMvc 테스트. 회원가입은 permitAll이고, 마이페이지 조회/수정/
 *       비밀번호변경/탈퇴는 대상 계정이 요청 바디가 아니라 JWT 인증 주체에서 오는지 확인한다
 *       (알려진 이슈 수정 — 다른 사용자 uId를 지정한 마이페이지 인가 우회 방지).
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성
 */
class UserControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserService userService;

    private String userToken(String uId) {
        return "Bearer " + jwtTokenProvider.createToken(uId, List.of("USER"));
    }

    @Test
    void 회원가입은_인증없이도_200() throws Exception {
        UserCreateRequestDto request = new UserCreateRequestDto();
        request.setUId("newuser");
        request.setUPwd("Password1!");
        request.setUName("새회원");
        request.setUEmail("newuser@example.com");

        when(userService.createUser(any())).thenReturn(1);

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));
    }

    @Test
    void 인증없이_마이페이지조회하면_401() throws Exception {
        mockMvc.perform(get("/api/user/mypage"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 인증된본인이_마이페이지조회하면_토큰의사용자ID로_조회한다() throws Exception {
        User user = new User();
        user.setUId("user01");
        user.setUName("홍길동");
        when(userService.getUserById("user01")).thenReturn(UserMypageResponseDto.from(user));

        mockMvc.perform(get("/api/user/mypage").header("Authorization", userToken("user01")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uId").value("user01"));
    }

    @Test
    void 비밀번호변경_본인확인은_토큰의사용자ID로_이루어진다() throws Exception {
        UserPasswordUpdateRequestDto request = new UserPasswordUpdateRequestDto();
        request.setCurrentPwd("OldPassword1!");
        request.setNewPwd("NewPassword1!");

        when(userService.changePassword(any(), eq("user01"))).thenReturn(1);

        mockMvc.perform(put("/api/user/password")
                        .header("Authorization", userToken("user01"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));
    }

    @Test
    void 회원탈퇴_본인확인은_토큰의사용자ID로_이루어진다() throws Exception {
        when(userService.removeUser("user01")).thenReturn(1);

        mockMvc.perform(delete("/api/user/withdraw").header("Authorization", userToken("user01")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));
    }
}
