package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;

import com.example.demo.dao.AdminTreatmentCategoryDao;
import com.example.demo.dao.TreatmentCategoryDao;
import com.example.demo.dto.response.treatment.CategoryResponseDto;
import com.example.demo.dto.request.treatment.CategoryCreateRequestDto;
import com.example.demo.dto.request.treatment.CategoryUpdateRequestDto;
import com.example.demo.entity.TreatmentCategory;

/**
 * 파일명: AdminTreatmentCategoryService.java
 * 설명: 관리자용 진료항목 카테고리 관련 서비스
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 클래스 생성
 */

@Service
public class AdminTreatmentCategoryService {

    @Autowired
    private AdminTreatmentCategoryDao categoryDao;

    // 카테고리 목록 조회
    public List<CategoryResponseDto> getAllCategories() {

        List<TreatmentCategory> categories = categoryDao.selectAllCategories();
        List<CategoryResponseDto> listResponse = new ArrayList<>();

        for(TreatmentCategory category : categories) {
            CategoryResponseDto response = new CategoryResponseDto();
            response.setCategoryId(category.getCategoryId());
            response.setCategoryName(category.getCategoryName());
            response.setIsVisible(category.getIsVisible());
            response.setCreatedAt(category.getCreatedAt());
            response.setUpdatedAt(category.getUpdatedAt());

            listResponse.add(response);
        }

        return listResponse;
    }

    // 카테고리 상세 조회
    public CategoryResponseDto getCategoryById(Integer categoryId) {

        TreatmentCategory category = categoryDao.selectCategoryById(categoryId);

        if(category == null) {
            throw new IllegalArgumentException("존재하지 않는 카테고리입니다.");
        }

        CategoryResponseDto response = new CategoryResponseDto();
        response.setCategoryId(category.getCategoryId());
        response.setCategoryName(category.getCategoryName());
        response.setIsVisible(category.getIsVisible());
        response.setCreatedAt(category.getCreatedAt());
        response.setUpdatedAt(category.getUpdatedAt());

        return response;
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
            throw new IllegalArgumentException("존재하지 않는 카테고리입니다.");
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
            throw new IllegalArgumentException("존재하지 않는 카테고리입니다.");
        }

        return categoryDao.deleteCategory(categoryId);
    }
}
