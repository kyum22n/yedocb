package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.dao.AdminTreatmentCategoryDao;
import com.example.demo.dto.response.treatment.CategoryResponseDto;
import com.example.demo.dto.request.treatment.CategoryCreateRequestDto;
import com.example.demo.dto.request.treatment.CategoryUpdateRequestDto;
import com.example.demo.entity.TreatmentCategory;
import com.example.demo.exception.ResourceNotFoundException;

/**
 * 파일명: AdminTreatmentCategoryService.java
 * 설명: 관리자용 진료항목 카테고리 관련 서비스
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 클래스 생성
 * 2026-09-06 | 리팩토링 | "존재하지 않는 카테고리입니다" -> ResourceNotFoundException 교체,
 *                        응답 DTO 매핑을 CategoryResponseDto.from(entity) 정적 팩토리 방식으로 변경 (Phase 2)
 */

@Service
public class AdminTreatmentCategoryService {

    @Autowired
    private AdminTreatmentCategoryDao categoryDao;

    // 카테고리 목록 조회
    public List<CategoryResponseDto> getAllCategories() {
        return categoryDao.selectAllCategories().stream()
                .map(CategoryResponseDto::from)
                .collect(Collectors.toList());
    }

    // 카테고리 상세 조회
    public CategoryResponseDto getCategoryById(Integer categoryId) {

        TreatmentCategory category = categoryDao.selectCategoryById(categoryId);

        if(category == null) {
            throw new ResourceNotFoundException("존재하지 않는 카테고리입니다.");
        }

        return CategoryResponseDto.from(category);
    }

    // 카테고리 생성
    public int createCategory(CategoryCreateRequestDto request) {

        TreatmentCategory category = new TreatmentCategory();
        category.setCategoryName(request.getCategoryName());
        category.setIsVisible(request.getIsVisible());

        return categoryDao.insertCategory(category);
    }

    // 카테고리 수정
    public int modifyCategory(CategoryUpdateRequestDto request) {

        TreatmentCategory existingCategory = categoryDao.selectCategoryById(request.getCategoryId());

        if(existingCategory == null) {
            throw new ResourceNotFoundException("존재하지 않는 카테고리입니다.");
        }

        TreatmentCategory category = new TreatmentCategory();
        category.setCategoryId(request.getCategoryId());
        category.setCategoryName(request.getCategoryName());
        category.setIsVisible(request.getIsVisible());

        return categoryDao.updateCategory(category);
    }

    // 카테고리 삭제
    public int removeCategory(Integer categoryId) {

        TreatmentCategory existingCategory = categoryDao.selectCategoryById(categoryId);

        if(existingCategory == null) {
            throw new ResourceNotFoundException("존재하지 않는 카테고리입니다.");
        }

        return categoryDao.deleteCategory(categoryId);
    }
}
