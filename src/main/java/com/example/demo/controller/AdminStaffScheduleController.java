package com.example.demo.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.schedule.AdminStaffScheduleCreateRequestDto;
import com.example.demo.dto.request.schedule.AdminStaffScheduleUpdateRequestDto;
import com.example.demo.dto.response.schedule.AdminStaffScheduleResponseDto;
import com.example.demo.service.AdminStaffScheduleService;

/**
 * 파일명: AdminStaffScheduleController.java
 * 설명: 관리자용 직원 일정 관련 controller
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-25 | 규민 | 클래스 생성
 */

@RestController
@RequestMapping("/admin/staff-schedules")
public class AdminStaffScheduleController {

    @Autowired
    private AdminStaffScheduleService staffScheduleService;

    // 직원 일정 등록
    @PostMapping("/register")
    public ResponseEntity<Integer> registerStaffSchedule(@RequestBody AdminStaffScheduleCreateRequestDto request) {
        return ResponseEntity.ok(staffScheduleService.createStaffSchedule(request));
    }

    // 직원 일정 목록 조회
    @GetMapping("/all")
    public ResponseEntity<List<AdminStaffScheduleResponseDto>> getStaffScheduleList() {
        return ResponseEntity.ok(staffScheduleService.getAllStaffSchedules());
    }

    // 관리자별 직원 일정 목록 조회
    @GetMapping("/admin")
    public ResponseEntity<List<AdminStaffScheduleResponseDto>> getStaffSchedulesByAdminId(
            @RequestParam("adminId") Integer adminId) {
        return ResponseEntity.ok(staffScheduleService.getStaffSchedulesByAdminId(adminId));
    }

    // 날짜별 직원 일정 목록 조회
    @GetMapping("/date")
    public ResponseEntity<List<AdminStaffScheduleResponseDto>> getStaffSchedulesByDate(
            @RequestParam("scheduleDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate scheduleDate) {
        return ResponseEntity.ok(staffScheduleService.getStaffSchedulesByDate(scheduleDate));
    }

    // 일정 유형별 직원 일정 목록 조회
    @GetMapping("/type")
    public ResponseEntity<List<AdminStaffScheduleResponseDto>> getStaffSchedulesByType(
            @RequestParam("scheduleType") String scheduleType) {
        return ResponseEntity.ok(staffScheduleService.getStaffSchedulesByType(scheduleType));
    }

    // 직원 일정 상세 조회
    @GetMapping("/{scheduleId}")
    public ResponseEntity<AdminStaffScheduleResponseDto> getStaffScheduleDetail(
            @PathVariable("scheduleId") Integer scheduleId) {
        return ResponseEntity.ok(staffScheduleService.getStaffScheduleById(scheduleId));
    }

    // 직원 일정 수정
    @PutMapping("/update")
    public ResponseEntity<Integer> updateStaffSchedule(@RequestBody AdminStaffScheduleUpdateRequestDto request) {
        return ResponseEntity.ok(staffScheduleService.modifyStaffSchedule(request));
    }

    // 직원 일정 삭제
    @DeleteMapping("/delete/{scheduleId}")
    public ResponseEntity<Integer> deleteStaffSchedule(@PathVariable("scheduleId") Integer scheduleId) {
        return ResponseEntity.ok(staffScheduleService.removeStaffSchedule(scheduleId));
    }
}
