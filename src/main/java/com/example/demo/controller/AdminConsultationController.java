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

import com.example.demo.dto.request.consultation.AdminConsultationConvertRequestDto;
import com.example.demo.dto.request.consultation.AdminConsultationCreateRequestDto;
import com.example.demo.dto.request.consultation.AdminConsultationStatusUpdateRequestDto;
import com.example.demo.dto.request.consultation.AdminConsultationUpdateRequestDto;
import com.example.demo.dto.response.consultation.AdminConsultationResponseDto;
import com.example.demo.service.AdminConsultationService;

/**
 * 파일명: AdminConsultationController.java
 * 설명: 관리자용 상담 관련 controller
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 클래스 생성
 */

@RestController
@RequestMapping("/admin/consultations")
public class AdminConsultationController {

    @Autowired
    private AdminConsultationService consultationService;

    // 상담 등록
    @PostMapping("/register")
    public ResponseEntity<Integer> registerConsultation(@RequestBody AdminConsultationCreateRequestDto request) {
        return ResponseEntity.ok(consultationService.createConsultation(request));
    }

    // 상담 목록 조회
    @GetMapping("/all")
    public ResponseEntity<List<AdminConsultationResponseDto>> getConsultationList() {
        return ResponseEntity.ok(consultationService.getAllConsultations());
    }

    // 회원별 상담 목록 조회
    @GetMapping("/member")
    public ResponseEntity<List<AdminConsultationResponseDto>> getConsultationsByMemberId(
            @RequestParam("memberId") Integer memberId) {
        return ResponseEntity.ok(consultationService.getConsultationsByMemberId(memberId));
    }

    // 담당자별 상담 목록 조회
    @GetMapping("/admin")
    public ResponseEntity<List<AdminConsultationResponseDto>> getConsultationsByAdminId(
            @RequestParam("adminId") Integer adminId) {
        return ResponseEntity.ok(consultationService.getConsultationsByAdminId(adminId));
    }

    // 상담 상태별 목록 조회
    @GetMapping("/status")
    public ResponseEntity<List<AdminConsultationResponseDto>> getConsultationsByStatus(
            @RequestParam("consultationStatus") String consultationStatus) {
        return ResponseEntity.ok(consultationService.getConsultationsByStatus(consultationStatus));
    }

    // 상담 상세 조회
    @GetMapping("/{consultationId}")
    public ResponseEntity<AdminConsultationResponseDto> getConsultationDetail(
            @PathVariable("consultationId") Integer consultationId) {
        return ResponseEntity.ok(consultationService.getConsultationById(consultationId));
    }

    // 상담 정보 수정
    @PutMapping("/update")
    public ResponseEntity<Integer> updateConsultation(@RequestBody AdminConsultationUpdateRequestDto request) {
        return ResponseEntity.ok(consultationService.modifyConsultation(request));
    }

    // 상담 상태 수정
    @PutMapping("/status/update")
    public ResponseEntity<Integer> updateConsultationStatus(
            @RequestBody AdminConsultationStatusUpdateRequestDto request) {
        return ResponseEntity.ok(consultationService.modifyConsultationStatus(request));
    }

    // 상담 예약 전환
    @PutMapping("/convert")
    public ResponseEntity<Integer> convertConsultationToReservation(
            @RequestBody AdminConsultationConvertRequestDto request) {
        return ResponseEntity.ok(consultationService.convertConsultationToReservation(request));
    }

    // 상담 삭제
    @DeleteMapping("/delete/{consultationId}")
    public ResponseEntity<Integer> deleteConsultation(@PathVariable("consultationId") Integer consultationId) {
        return ResponseEntity.ok(consultationService.removeConsultation(consultationId));
    }
}
