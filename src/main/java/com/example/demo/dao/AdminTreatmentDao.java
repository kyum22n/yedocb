package com.example.demo.dao;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.entity.Treatment;
import java.util.List;

/**
 * 파일명: AdminTreatmentDao.java
 * 설명: 관리자용 진료항목 관련 DAO 인터페이스
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 인터페이스 생성
 */

@Mapper
public interface AdminTreatmentDao {

    // 진료항목 목록 조회
    public List<Treatment> selectAllTreatments();

    // 카테고리별 항목 조회
    public List<Treatment> selectTreatmentsByCategoryId(Integer categoryId);

    // 진료항목 조회
    public Treatment selectTreatmentById(Integer treatmentId);
    
    // 진료항목 추가
    public int insertTreatment(Treatment treatment);

    // 진료항목 수정
    public int updateTreatment(Treatment treatment);

    // 진료항목 삭제
    public int deleteTreatment(Integer treatmentId);
}
