package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

import com.example.demo.dto.request.admin.AdminPasswordUpdateRequestDto;
import com.example.demo.dto.request.admin.AdminUpdateRequestDto;
import com.example.demo.entity.Admin;
import com.example.demo.service.AdminService;


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

    // 관리자 목록 조회
    @GetMapping("/list")
    public List<Admin> getAllAdmins() {
        return adminService.getAllAdmins();
    }

    // 관리자 상세 조회
    @GetMapping("/detail")
    public Admin getAdminDetail(@RequestParam("adminId") Integer adminId) {
        return adminService.getAdminById(adminId);
    }
    
    // 관리자 등록
    @PostMapping("/register")
    public int registerAdmin(@RequestBody Admin admin) {
        return adminService.createAdmin(admin);
    }

    // 관리자 정보 수정
    @PutMapping("/update")
    public int updateAdmin(@RequestBody AdminUpdateRequestDto admin) {
        return adminService.modifyAdmin(admin);
    }

    // 관리자 삭제
    @DeleteMapping("/delete/{adminId}")
    public int deleteAdmin(@PathVariable("adminId") Integer adminId) {
        return adminService.removeAdmin(adminId);
    }

    // 관리자 비밀번호 변경
    @PutMapping("/update-password")
    public int updateAdminPassword(@RequestBody AdminPasswordUpdateRequestDto newPassword) {
        return adminService.modifyAdminPassword(newPassword);   
    }
    
}
