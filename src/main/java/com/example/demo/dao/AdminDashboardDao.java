package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.dto.response.inquiry.AdminInquiryResponseDto;
import com.example.demo.dto.response.reservation.AdminReservationResponseDto;

/**
 * 파일명: AdminDashboardDao.java
 * 설명: 관리자용 대시보드 관련 DAO 인터페이스
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-25 | 규민 | 인터페이스 생성
 */

@Mapper
public interface AdminDashboardDao {

    // 오늘 예약 수 조회
    public Integer selectTodayReservationCount();

    // 대기 예약 수 조회
    public Integer selectPendingReservationCount();

    // 오늘 상담 수 조회
    public Integer selectTodayConsultationCount();

    // 답변 대기 문의 수 조회
    public Integer selectWaitingInquiryCount();

    // PMS 연동 실패 수 조회
    public Integer selectPmsFailedCount();

    // 최근 예약 목록 조회
    public List<AdminReservationResponseDto> selectRecentReservations();

    // 최근 문의 목록 조회
    public List<AdminInquiryResponseDto> selectRecentInquiries();
}
