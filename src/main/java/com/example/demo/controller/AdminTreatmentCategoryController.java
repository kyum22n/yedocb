package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

import com.example.demo.service.AdminTreatmentCategoryService;
import com.example.demo.dto.request.treatment.CategoryCreateRequestDto;
import com.example.demo.dto.request.treatment.CategoryUpdateRequestDto;
import com.example.demo.dto.request.treatment.CategoryDeleteRequestDto;
import com.example.demo.dto.response.treatment.CategoryResponseDto;

/**
 * 파일명: AdminTreatmentCategoryController.java
 * 설명: 관리자용 진료항목 카테고리 관련 controller
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 클래스 생성
 */

@RestController
@RequestMapping("/admin/treatment-categories")
public class AdminTreatmentCategoryController {

    @Autowired
    private AdminTreatmentCategoryService categoryService;

    // 카테고리 목록 조회
    @GetMapping("/all")
    public ResponseEntity<List<CategoryResponseDto>> getCategoryList() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    // 카테고리 상세 조회
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponseDto> getCategoryDetail(@PathVariable("categoryId") Integer categoryId) {
        return ResponseEntity.ok(categoryService.getCategoryById(categoryId));
    }

    // 카테고리 등록
    @PostMapping("/register")
    public ResponseEntity<Integer> registerCategory(@RequestBody CategoryCreateRequestDto request) {
        return ResponseEntity.ok(categoryService.createCategory(request));
    }

    // 카테고리 수정
    @PutMapping("/update")
    public ResponseEntity<Integer> updateCategory(@RequestBody CategoryUpdateRequestDto request) {
        return ResponseEntity.ok(categoryService.modifyCategory(request));
    }

    // 카테고리 삭제
    @DeleteMapping("/delete/{categoryId}")
    public ResponseEntity<Integer> deleteCategory(@PathVariable("categoryId") Integer categoryId) {
        return ResponseEntity.ok(categoryService.removeCategory(categoryId));
    }

}
