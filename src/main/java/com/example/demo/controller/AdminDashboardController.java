package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.response.dashboard.AdminDashboardResponseDto;
import com.example.demo.service.AdminDashboardService;

/**
 * 파일명: AdminDashboardController.java
 * 설명: 관리자용 대시보드 관련 controller
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-25 | 규민 | 클래스 생성
 */

@RestController
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {

    @Autowired
    private AdminDashboardService dashboardService;

    // 대시보드 조회
    @GetMapping
    public ResponseEntity<AdminDashboardResponseDto> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboard());
    }
}
