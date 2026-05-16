package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.example.demo.dto.request.reservation.AdminPmsSyncStatusUpdateRequestDto;
import com.example.demo.dto.request.reservation.AdminReservationCreateRequestDto;
import com.example.demo.dto.request.reservation.AdminReservationStatusUpdateRequestDto;
import com.example.demo.dto.request.reservation.AdminReservationUpdateRequestDto;
import com.example.demo.dto.response.reservation.AdminReservationResponseDto;
import com.example.demo.service.AdminReservationService;

/**
 * 파일명: AdminReservationController.java
 * 설명: 관리자용 예약 관련 controller
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 클래스 생성
 */

@RestController
@RequestMapping("/admin/reservations")
public class AdminReservationController {

    @Autowired
    private AdminReservationService reservationService;

    // 예약 등록
    @PostMapping("/register")
    public ResponseEntity<Integer> registerReservation(@RequestBody AdminReservationCreateRequestDto request) {
        return ResponseEntity.ok(reservationService.createReservation(request));
    }

    // 예약 목록 조회
    @GetMapping("/all")
    public ResponseEntity<List<AdminReservationResponseDto>> getReservationList() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    // 회원별 예약 목록 조회
    @GetMapping("/member")
    public ResponseEntity<List<AdminReservationResponseDto>> getReservationsByMemberId(
            @RequestParam("memberId") Integer memberId) {
        return ResponseEntity.ok(reservationService.getReservationsByMemberId(memberId));
    }

    // 담당자별 예약 목록 조회
    @GetMapping("/admin")
    public ResponseEntity<List<AdminReservationResponseDto>> getReservationsByAdminId(
            @RequestParam("adminId") Integer adminId) {
        return ResponseEntity.ok(reservationService.getReservationsByAdminId(adminId));
    }

    // 예약 상태별 목록 조회
    @GetMapping("/status")
    public ResponseEntity<List<AdminReservationResponseDto>> getReservationsByStatus(
            @RequestParam("reservationStatus") String reservationStatus) {
        return ResponseEntity.ok(reservationService.getReservationsByStatus(reservationStatus));
    }

    // PMS 연동 상태별 목록 조회
    @GetMapping("/pms-status")
    public ResponseEntity<List<AdminReservationResponseDto>> getReservationsByPmsSyncStatus(
            @RequestParam("pmsSyncStatus") String pmsSyncStatus) {
        return ResponseEntity.ok(reservationService.getReservationsByPmsSyncStatus(pmsSyncStatus));
    }

    // 예약 상세 조회
    @GetMapping("/{reservationId}")
    public ResponseEntity<AdminReservationResponseDto> getReservationDetail(
            @PathVariable("reservationId") Integer reservationId) {
        return ResponseEntity.ok(reservationService.getReservationById(reservationId));
    }

    // 예약 정보 수정
    @PutMapping("/update")
    public ResponseEntity<Integer> updateReservation(@RequestBody AdminReservationUpdateRequestDto request) {
        return ResponseEntity.ok(reservationService.modifyReservation(request));
    }

    // 예약 상태 수정
    @PutMapping("/status/update")
    public ResponseEntity<Integer> updateReservationStatus(
            @RequestBody AdminReservationStatusUpdateRequestDto request) {
        return ResponseEntity.ok(reservationService.modifyReservationStatus(request));
    }

    // PMS 연동 상태 수정
    @PutMapping("/pms-status/update")
    public ResponseEntity<Integer> updatePmsSyncStatus(@RequestBody AdminPmsSyncStatusUpdateRequestDto request) {
        return ResponseEntity.ok(reservationService.modifyPmsSyncStatus(request));
    }

    // 예약 삭제
    @DeleteMapping("/delete/{reservationId}")
    public ResponseEntity<Integer> deleteReservation(@PathVariable("reservationId") Integer reservationId) {
        return ResponseEntity.ok(reservationService.removeReservation(reservationId));
    }
}
