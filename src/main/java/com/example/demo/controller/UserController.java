package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.user.UserCreateRequestDto;
import com.example.demo.dto.request.user.UserMypageUpdateRequestDto;
import com.example.demo.dto.request.user.UserPasswordUpdateRequestDto;
import com.example.demo.dto.response.user.UserMypageResponseDto;
import com.example.demo.service.UserService;

import jakarta.validation.Valid;

/**
 * 파일명: UserController.java
 * 설명: 회원 정보 관련 controller (구 MemberController 대체). 기본 경로를 /api/user로 변경하여
 *       B의 로그인/JWT/OAuth 인프라가 기대하는 API 경로 컨벤션에 맞춤.
 *       마이페이지 조회/수정/탈퇴/비밀번호변경의 대상 계정은 쿼리 파라미터가 아니라
 *       JWT 인증 주체(Authentication)에서 가져온다 — 다른 사용자의 uId를 지정해
 *       정보를 열람/수정/탈퇴시킬 수 있던 문제를 막기 위함.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | Member -> User 통합, /member -> /api/user 경로 변경 (Phase 1)
 * 2026-09-06 | 리팩토링 | uId를 쿼리 파라미터 대신 인증 주체에서 획득하도록 변경 (알려진 이슈 정리),
 *                        비밀번호 변경 엔드포인트 추가 (프론트엔드 세션 요청 대응)
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<Integer> registerUser(@Valid @RequestBody UserCreateRequestDto request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    // 마이페이지 조회 (인증된 본인)
    @GetMapping("/mypage")
    public ResponseEntity<UserMypageResponseDto> getMyPage(Authentication authentication) {
        return ResponseEntity.ok(userService.getUserById(authentication.getName()));
    }

    // 마이페이지 수정 (인증된 본인)
    @PutMapping("/mypage/update")
    public ResponseEntity<Integer> updateMyPage(
            @Validated @RequestBody UserMypageUpdateRequestDto request,
            Authentication authentication) {
        return ResponseEntity.ok(userService.modifyUser(request, authentication.getName()));
    }

    // 비밀번호 변경 (인증된 본인, 현재 비밀번호 확인 필요)
    @PutMapping("/password")
    public ResponseEntity<Integer> updatePassword(
            @Valid @RequestBody UserPasswordUpdateRequestDto request,
            Authentication authentication) {
        return ResponseEntity.ok(userService.changePassword(request, authentication.getName()));
    }

    // 회원 탈퇴 (인증된 본인)
    @DeleteMapping("/withdraw")
    public ResponseEntity<Integer> withdrawUser(Authentication authentication) {
        return ResponseEntity.ok(userService.removeUser(authentication.getName()));
    }
}
