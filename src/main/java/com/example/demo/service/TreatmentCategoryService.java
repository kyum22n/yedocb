package com.example.demo.service;

import org.springframework.stereotype.Service;
import com.example.demo.dao.TreatmentCategoryDao;
import com.example.demo.dto.response.treatment.CategoryResponseDto;
import com.example.demo.entity.TreatmentCategory;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.List;

/**
 * 파일명: TreatmentCategoryService.java
 * 설명: 사용자용 진료항목 카테고리 관련 서비스
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 클래스 생성
 */

@Service
public class TreatmentCategoryService {

    @Autowired
    private TreatmentCategoryDao categoryDao;

    // 카테고리 목록 조회
    public List<CategoryResponseDto> getAllVisibleCategories() {

        List<TreatmentCategory> categories = categoryDao.selectVisibleCategories();
        List<CategoryResponseDto> listResponse = new ArrayList<>();

        for(TreatmentCategory category : categories) {
            CategoryResponseDto response = new CategoryResponseDto();
            response.setCategoryId(category.getCategoryId());
            response.setCategoryName(category.getCategoryName());

            listResponse.add(response);
        }

        return listResponse;
    }

    // 카테고리 상세 조회
    public CategoryResponseDto getVisibleCategoryById(Integer categoryId) {

        TreatmentCategory category = categoryDao.selectVisibleCategoryById(categoryId);

        if(category == null) {
            throw new IllegalArgumentException("존재하지 않는 카테고리입니다.");
        }

        CategoryResponseDto response = new CategoryResponseDto();
        response.setCategoryId(category.getCategoryId());
        response.setCategoryName(category.getCategoryName());

        return response;
    }
}
