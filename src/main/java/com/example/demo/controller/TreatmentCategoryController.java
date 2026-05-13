package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.TreatmentCategoryService;
import com.example.demo.dto.response.treatment.CategoryResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

/**
 * 파일명: TreatmentCategoryController.java
 * 설명: 사용자용 진료항목 카테고리 관련 controller
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 클래스 생성
 */

@RestController
@RequestMapping("/treatment-categories")
public class TreatmentCategoryController {

    @Autowired
    private TreatmentCategoryService categoryService;

    // 카테고리 목록 조회
    @GetMapping("/all")
    public ResponseEntity<List<CategoryResponseDto>> getVisibleCategoryList() {
        return ResponseEntity.ok(categoryService.getAllVisibleCategories());
    }

    // 카테고리 상세 조회
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponseDto> getVisibleCategoryDetail(@PathVariable("categoryId") Integer categoryId) {
        return ResponseEntity.ok(categoryService.getVisibleCategoryById(categoryId));
    }

}
