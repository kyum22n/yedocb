package com.example.demo.dao;

import org.apache.ibatis.annotations.Mapper;
import com.example.demo.entity.Treatment;
import java.util.List;

/**
 * 파일명: TreatmentDao.java
 * 설명: 진료항목 관련 DAO 인터페이스 (사용자용)
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 인터페이스 생성
 */

@Mapper
public interface TreatmentDao {

    // 진료항목 목록 조회
    public List<Treatment> selectVisibleTreatments();

    // 카테고리별 항목 조회
    public List<Treatment> selectVisibleTreatmentsByCategoryId(Integer categoryId);
    
    // 진료항목 조회
    public Treatment selectVisibleTreatmentById(Integer treatmentId);

}
