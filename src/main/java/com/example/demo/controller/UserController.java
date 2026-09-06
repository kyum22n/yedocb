package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.user.UserCreateRequestDto;
import com.example.demo.dto.request.user.UserMypageUpdateRequestDto;
import com.example.demo.dto.response.user.UserMypageResponseDto;
import com.example.demo.service.UserService;

import jakarta.validation.Valid;

/**
 * 파일명: UserController.java
 * 설명: 회원 정보 관련 controller (구 MemberController 대체). 기본 경로를 /api/user로 변경하여
 *       B의 로그인/JWT/OAuth 인프라가 기대하는 API 경로 컨벤션에 맞춤.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | Member -> User 통합, /member -> /api/user 경로 변경 (Phase 1)
 *
 * TODO: JWT 인증이 연동되면 마이페이지 조회/수정/탈퇴의 uId는 쿼리 파라미터 대신
 *       SecurityContextHolder / @AuthenticationPrincipal로 획득한 인증 주체에서 가져오도록 변경할 것.
 *       (현재는 인증 인프라가 별도 세션에서 병행 작업 중이라 임시로 쿼리 파라미터를 사용함)
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

    // 마이페이지 조회
    // TODO: uId를 쿼리 파라미터 대신 인증 주체(SecurityContextHolder)에서 획득하도록 변경 예정
    @GetMapping("/mypage")
    public ResponseEntity<UserMypageResponseDto> getMyPage(@RequestParam("uId") String uId) {
        return ResponseEntity.ok(userService.getUserById(uId));
    }

    // 마이페이지 수정
    @PutMapping("/mypage/update")
    public ResponseEntity<Integer> updateMyPage(@Validated @RequestBody UserMypageUpdateRequestDto request) {
        return ResponseEntity.ok(userService.modifyUser(request));
    }

    // 회원 탈퇴
    @DeleteMapping("/withdraw")
    public ResponseEntity<Integer> withdrawUser(@RequestParam("uId") String uId) {
        return ResponseEntity.ok(userService.removeUser(uId));
    }
}
