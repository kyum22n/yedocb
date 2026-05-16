package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.response.treatment.TreatmentResponseDto;
import com.example.demo.service.TreatmentService;

/**
 * 파일명: TreatmentController.java
 * 설명: 사용자용 진료항목 관련 controller
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 클래스 생성
 */

@RestController
@RequestMapping("/treatments")
public class TreatmentController {

    @Autowired
    private TreatmentService treatmentService;

    // 진료항목 목록 조회
    @GetMapping("/all")
    public ResponseEntity<List<TreatmentResponseDto>> getVisibleTreatmentList() {
        return ResponseEntity.ok(treatmentService.getVisibleTreatments());
    }

    // 카테고리별 진료항목 조회
    @GetMapping("/category")
    public ResponseEntity<List<TreatmentResponseDto>> getVisibleTreatmentsByCategoryId(
            @RequestParam("categoryId") Integer categoryId) {
        return ResponseEntity.ok(treatmentService.getVisibleTreatmentsByCategoryId(categoryId));
    }

    // 진료항목 상세 조회
    @GetMapping("/{treatmentId}")
    public ResponseEntity<TreatmentResponseDto> getVisibleTreatmentDetail(
            @PathVariable("treatmentId") Integer treatmentId) {
        return ResponseEntity.ok(treatmentService.getVisibleTreatmentById(treatmentId));
    }
}
