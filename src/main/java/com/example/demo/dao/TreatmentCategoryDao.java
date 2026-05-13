package com.example.demo.dao;

import org.apache.ibatis.annotations.Mapper;
import com.example.demo.entity.TreatmentCategory;
import java.util.List;

/**
 * 파일명: TreatmentCategoryDao.java
 * 설명: 사용자용 진료항목 카테고리 관련 dao
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 인터페이스 생성
 */

@Mapper
public interface TreatmentCategoryDao {
    // 카테고리 목록 조회
    public List<TreatmentCategory> selectVisibleCategories();
    // 카테고리 상세 조회
    public TreatmentCategory selectVisibleCategoryById(Integer categoryId);
}
