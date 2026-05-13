package com.example.demo.dao;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import com.example.demo.entity.TreatmentCategory;

/**
 * 파일명: AdminTreatmentCategoryDao.java
 * 설명: 관리자용 진료항목 카테고리 관련 dao
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 인터페이스 생성
 */

@Mapper
public interface AdminTreatmentCategoryDao {

    // 카테고리 목록 조회
    public List<TreatmentCategory> selectAllCategories();
    // 카테고리 상세 조회
    public TreatmentCategory selectCategoryById(Integer categoryId);
    // 카테고리 생성
    public int insertCategory(TreatmentCategory category);
    // 카테고리 수정
    public int updateCategory(TreatmentCategory category);
    // 카테고리 삭제
    public int deleteCategory(Integer categoryId);
}
