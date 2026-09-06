package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dao.AdminDao;
import com.example.demo.dto.request.auth.AdminLoginRequestDto;
import com.example.demo.dto.response.auth.TokenResponseDto;
import com.example.demo.entity.Admin;
import com.example.demo.exception.InvalidCredentialsException;
import com.example.demo.security.JwtTokenProvider;

import jakarta.validation.Valid;

/**
 * 파일명: AdminLoginController.java
 * 설명: 관리자 로그인. adminRole(ADMIN/SUPERADMIN)을 JWT roles 클레임에 담는다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | 최초 생성 (com.example.yedocb 참고 이식)
 */
@RestController
@RequestMapping("/api/admin")
public class AdminLoginController {

    @Autowired
    private AdminDao adminDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> loginAdmin(@RequestBody @Valid AdminLoginRequestDto request) {
        Admin admin = adminDao.selectAdminByLoginId(request.getAdminLoginId());
        if (admin == null || !passwordEncoder.matches(request.getAdminPassword(), admin.getAdminPassword())) {
            throw new InvalidCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        String accessToken = jwtTokenProvider.createToken(admin.getAdminLoginId(), List.of(admin.getAdminRole()));
        return ResponseEntity.ok(TokenResponseDto.ofAccessOnly(accessToken, admin.getAdminLoginId()));
    }
}
