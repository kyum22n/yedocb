package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.entity.Consultation;

/**
 * 파일명: ConsultationDao.java
 * 설명: 사용자용 상담 관련 DAO 인터페이스
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 인터페이스 생성
 */

@Mapper
public interface ConsultationDao {

    // 상담 등록
    public int insertConsultation(Consultation consultation);

    // 회원별 상담 목록 조회
    public List<Consultation> selectConsultationsByMemberId(Integer memberId);

    // 상담 상세 조회
    public Consultation selectConsultationById(@Param("consultationId") Integer consultationId,
                                               @Param("memberId") Integer memberId);

    // 상담 수정
    public int updateConsultation(Consultation consultation);

    // 상담 취소
    public int cancelConsultation(@Param("consultationId") Integer consultationId,
                                  @Param("memberId") Integer memberId);
}
