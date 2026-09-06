package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

import com.example.demo.dto.request.admin.AdminCreateRequestDto;
import com.example.demo.dto.request.admin.AdminPasswordUpdateRequestDto;
import com.example.demo.dto.request.admin.AdminUpdateRequestDto;
import com.example.demo.dto.response.admin.AdminDetailResponseDto;
import com.example.demo.dto.response.admin.AdminListResponseDto;
import com.example.demo.service.AdminService;

import jakarta.validation.Valid;


/**
 * 파일명: AdminController.java
 * 설명: 관리자 정보 관련 controller
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 로그인 메소드 분리
 */

@RestController
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private AdminService adminService;

    // 관리자 목록 조회 (비밀번호 해시가 포함되지 않는 DTO 리스트로 반환)
    @GetMapping("/list")
    public ResponseEntity<List<AdminListResponseDto>> getAllAdmins() {
        return ResponseEntity.ok(adminService.getAllAdmins());
    }

    // 관리자 상세 조회
    @GetMapping("/detail")
    public ResponseEntity<AdminDetailResponseDto> getAdminDetail(@RequestParam("adminId") Integer adminId) {
        return ResponseEntity.ok(adminService.getAdminById(adminId));
    }

    // 관리자 등록
    @PostMapping("/register")
    public ResponseEntity<Integer> registerAdmin(@Valid @RequestBody AdminCreateRequestDto request) {
        return ResponseEntity.ok(adminService.createAdmin(request));
    }

    // 관리자 정보 수정
    @PutMapping("/update")
    public ResponseEntity<Integer> updateAdmin(@Valid @RequestBody AdminUpdateRequestDto admin) {
        return ResponseEntity.ok(adminService.modifyAdmin(admin));
    }

    // 관리자 삭제
    @DeleteMapping("/delete/{adminId}")
    public ResponseEntity<Integer> deleteAdmin(@PathVariable("adminId") Integer adminId) {
        return ResponseEntity.ok(adminService.removeAdmin(adminId));
    }
    
}
