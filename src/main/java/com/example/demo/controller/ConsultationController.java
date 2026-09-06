package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.consultation.ConsultationCancelRequestDto;
import com.example.demo.dto.request.consultation.ConsultationCreateRequestDto;
import com.example.demo.dto.request.consultation.ConsultationUpdateRequestDto;
import com.example.demo.dto.response.consultation.ConsultationResponseDto;
import com.example.demo.service.ConsultationService;

import jakarta.validation.Valid;

/**
 * 파일명: ConsultationController.java
 * 설명: 사용자용 상담 관련 controller
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 클래스 생성
 */

@RestController
@RequestMapping("/consultations")
public class ConsultationController {

    @Autowired
    private ConsultationService consultationService;

    // 상담 등록
    @PostMapping("/register")
    public ResponseEntity<Integer> registerConsultation(@Valid @RequestBody ConsultationCreateRequestDto request) {
        return ResponseEntity.ok(consultationService.createConsultation(request));
    }

    // 회원별 상담 목록 조회
    @GetMapping("/member")
    public ResponseEntity<List<ConsultationResponseDto>> getConsultationsByUId(
            @RequestParam("uId") String uId) {
        return ResponseEntity.ok(consultationService.getConsultationsByUId(uId));
    }

    // 상담 상세 조회
    @GetMapping("/{consultationId}")
    public ResponseEntity<ConsultationResponseDto> getConsultationDetail(
            @PathVariable("consultationId") Integer consultationId,
            @RequestParam("uId") String uId) {
        return ResponseEntity.ok(consultationService.getConsultationById(consultationId, uId));
    }

    // 상담 정보 수정
    @PutMapping("/update")
    public ResponseEntity<Integer> updateConsultation(@Valid @RequestBody ConsultationUpdateRequestDto request) {
        return ResponseEntity.ok(consultationService.modifyConsultation(request));
    }

    // 상담 취소
    @PutMapping("/cancel")
    public ResponseEntity<Integer> cancelConsultation(@Valid @RequestBody ConsultationCancelRequestDto request) {
        return ResponseEntity.ok(consultationService.cancelConsultation(request));
    }
}
