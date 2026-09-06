package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.user.UserCreateRequestDto;
import com.example.demo.dto.response.user.AdminUserDetailResponseDto;
import com.example.demo.dto.response.user.AdminUserListResponseDto;
import com.example.demo.service.AdminUserService;

import jakarta.validation.Valid;

/**
 * 파일명: AdminUserController.java
 * 설명: 관리자 회원 정보 관련 controller (구 AdminMemberController 대체)
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | Member -> User 통합, /admin/member -> /api/admin/user 경로 변경.
 *                        누락되어 있던 DTO import 추가, GET /list가 List&lt;User&gt;(비밀번호 해시 포함)를
 *                        그대로 반환하던 버그를 수정하여 List&lt;AdminUserListResponseDto&gt;를 반환하도록 함 (Phase 1)
 */
@RestController
@RequestMapping("/api/admin/user")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    // 관리자 회원 등록
    @PostMapping("/register")
    public ResponseEntity<Integer> registerAdminUser(@Valid @RequestBody UserCreateRequestDto request) {
        return ResponseEntity.ok(adminUserService.createAdminUser(request));
    }

    // 관리자 회원 목록 조회 (비밀번호 해시가 포함되지 않는 DTO 리스트로 반환)
    @GetMapping("/list")
    public ResponseEntity<List<AdminUserListResponseDto>> getAllUsers() {
        return ResponseEntity.ok(adminUserService.getAllUsers());
    }

    // 관리자 회원 상세 조회
    @GetMapping("/detail")
    public ResponseEntity<AdminUserDetailResponseDto> getUserDetail(@RequestParam("uId") String uId) {
        return ResponseEntity.ok(adminUserService.getAdminUserById(uId));
    }

    // 관리자 회원 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<Integer> deleteUser(@RequestParam("uId") String uId) {
        return ResponseEntity.ok(adminUserService.removeAdminUser(uId));
    }
}
