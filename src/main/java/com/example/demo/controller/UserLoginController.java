package com.example.demo.controller;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dao.UserDao;
import com.example.demo.dto.request.auth.UserLoginRequestDto;
import com.example.demo.dto.response.auth.TokenResponseDto;
import com.example.demo.entity.User;
import com.example.demo.exception.InvalidCredentialsException;
import com.example.demo.security.JwtTokenProvider;

import jakarta.validation.Valid;

/**
 * 파일명: UserLoginController.java
 * 설명: 사용자 로그인/토큰 재발급. 리프레시 토큰은 인메모리(ConcurrentHashMap)로 관리한다.
 *       운영 배포(Render) 재시작 시 로그인이 풀리는 한계가 있음 — docs/refactor-log.md 참고,
 *       이번 리팩토링 범위에서는 코드 변경 없이 문서화만 한다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | 최초 생성 (com.example.yedocb 참고 이식)
 */
@RestController
@RequestMapping("/api/user")
public class UserLoginController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // 운영 시 반드시 DB 또는 Redis 사용해야 함 (Render 재배포/재시작 시 전체 로그아웃됨)
    private final ConcurrentMap<String, String> refreshTokenStore = new ConcurrentHashMap<>();

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> loginUser(@RequestBody @Valid UserLoginRequestDto request) {
        User user = userDao.selectUserById(request.getUId());
        if (user == null || !passwordEncoder.matches(request.getUPwd(), user.getUPwd())) {
            throw new InvalidCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        String accessToken = jwtTokenProvider.createToken(user.getUId(), List.of("USER"));
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUId());
        refreshTokenStore.put(user.getUId(), refreshToken);

        return ResponseEntity.ok(TokenResponseDto.of(accessToken, refreshToken, user.getUId()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refresh(@RequestParam("refreshToken") String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new InvalidCredentialsException("리프레시 토큰이 유효하지 않습니다.");
        }

        String userId = jwtTokenProvider.getUserId(refreshToken);
        String storedToken = refreshTokenStore.get(userId);
        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new InvalidCredentialsException("리프레시 토큰이 일치하지 않습니다.");
        }

        String newAccessToken = jwtTokenProvider.createToken(userId, List.of("USER"));
        return ResponseEntity.ok(TokenResponseDto.of(newAccessToken, refreshToken, userId));
    }
}
