package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.entity.Consultation;

/**
 * 파일명: AdminConsultationDao.java
 * 설명: 관리자용 상담 관련 DAO 인터페이스
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 인터페이스 생성
 */

@Mapper
public interface AdminConsultationDao {

    // 상담 등록
    public int insertConsultation(Consultation consultation);

    // 상담 목록 조회
    public List<Consultation> selectAllConsultations();

    // 회원별 상담 목록 조회
    public List<Consultation> selectConsultationsByUId(String uId);

    // 담당자별 상담 목록 조회
    public List<Consultation> selectConsultationsByAdminId(Integer adminId);

    // 상담 상태별 목록 조회
    public List<Consultation> selectConsultationsByStatus(String consultationStatus);

    // 상담 상세 조회
    public Consultation selectConsultationById(Integer consultationId);

    // 상담 정보 수정
    public int updateConsultation(Consultation consultation);

    // 상담 상태 수정
    public int updateConsultationStatus(Consultation consultation);

    // 상담 예약 전환 처리
    public int convertConsultationToReservation(Consultation consultation);

    // 상담 삭제
    public int deleteConsultation(Integer consultationId);
}
