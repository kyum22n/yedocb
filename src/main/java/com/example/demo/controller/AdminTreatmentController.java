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

import com.example.demo.dto.request.treatment.TreatmentCreateRequestDto;
import com.example.demo.dto.request.treatment.TreatmentUpdateRequestDto;
import com.example.demo.dto.response.treatment.AdminTreatmentResponseDto;
import com.example.demo.service.AdminTreatmentService;

/**
 * 파일명: AdminTreatmentController.java
 * 설명: 관리자용 진료항목 관련 controller
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 클래스 생성
 */

@RestController
@RequestMapping("/admin/treatments")
public class AdminTreatmentController {

    @Autowired
    private AdminTreatmentService treatmentService;

    // 진료항목 목록 조회
    @GetMapping("/all")
    public ResponseEntity<List<AdminTreatmentResponseDto>> getTreatmentList() {
        return ResponseEntity.ok(treatmentService.getAllTreatments());
    }

    // 카테고리별 진료항목 조회
    @GetMapping("/category")
    public ResponseEntity<List<AdminTreatmentResponseDto>> getTreatmentsByCategoryId(
            @RequestParam("categoryId") Integer categoryId) {
        return ResponseEntity.ok(treatmentService.getTreatmentsByCategoryId(categoryId));
    }

    // 진료항목 상세 조회
    @GetMapping("/{treatmentId}")
    public ResponseEntity<AdminTreatmentResponseDto> getTreatmentDetail(
            @PathVariable("treatmentId") Integer treatmentId) {
        return ResponseEntity.ok(treatmentService.getTreatmentById(treatmentId));
    }

    // 진료항목 등록
    @PostMapping("/register")
    public ResponseEntity<Integer> registerTreatment(@RequestBody TreatmentCreateRequestDto request) {
        return ResponseEntity.ok(treatmentService.createTreatment(request));
    }

    // 진료항목 수정
    @PutMapping("/update")
    public ResponseEntity<Integer> updateTreatment(@RequestBody TreatmentUpdateRequestDto request) {
        return ResponseEntity.ok(treatmentService.updateTreatment(request));
    }

    // 진료항목 삭제
    @DeleteMapping("/delete/{treatmentId}")
    public ResponseEntity<Integer> deleteTreatment(@PathVariable("treatmentId") Integer treatmentId) {
        return ResponseEntity.ok(treatmentService.deleteTreatment(treatmentId));
    }
}
