package com.example.demo.service;

import org.springframework.stereotype.Service;
import com.example.demo.dao.TreatmentCategoryDao;
import com.example.demo.dto.response.treatment.CategoryResponseDto;
import com.example.demo.entity.TreatmentCategory;
import com.example.demo.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 파일명: TreatmentCategoryService.java
 * 설명: 사용자용 진료항목 카테고리 관련 서비스
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 클래스 생성
 * 2026-09-06 | 리팩토링 | "존재하지 않는 카테고리입니다" -> ResourceNotFoundException 교체,
 *                        응답 DTO 매핑을 CategoryResponseDto.from(entity) 정적 팩토리 방식으로 변경 (Phase 2)
 */

@Service
public class TreatmentCategoryService {

    @Autowired
    private TreatmentCategoryDao categoryDao;

    // 카테고리 목록 조회
    public List<CategoryResponseDto> getAllVisibleCategories() {
        return categoryDao.selectVisibleCategories().stream()
                .map(CategoryResponseDto::from)
                .collect(Collectors.toList());
    }

    // 카테고리 상세 조회
    public CategoryResponseDto getVisibleCategoryById(Integer categoryId) {

        TreatmentCategory category = categoryDao.selectVisibleCategoryById(categoryId);

        if(category == null) {
            throw new ResourceNotFoundException("존재하지 않는 카테고리입니다.");
        }

        return CategoryResponseDto.from(category);
    }
}
