package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.dto.request.statistics.StatisticsSearchRequestDto;
import com.example.demo.dto.response.statistics.ConsultationStatisticsResponseDto;
import com.example.demo.dto.response.statistics.InquiryStatisticsResponseDto;
import com.example.demo.dto.response.statistics.ReservationStatisticsResponseDto;
import com.example.demo.dto.response.statistics.TreatmentStatisticsResponseDto;

/**
 * 파일명: AdminStatisticsDao.java
 * 설명: 관리자용 통계/리포트 관련 DAO 인터페이스
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-25 | 규민 | 인터페이스 생성
 */

@Mapper
public interface AdminStatisticsDao {

    // 예약 통계 조회
    public ReservationStatisticsResponseDto selectReservationStatistics(StatisticsSearchRequestDto request);

    // 상담 통계 조회
    public ConsultationStatisticsResponseDto selectConsultationStatistics(StatisticsSearchRequestDto request);

    // 문의 통계 조회
    public InquiryStatisticsResponseDto selectInquiryStatistics(StatisticsSearchRequestDto request);

    // 인기 진료 항목 통계 조회
    public List<TreatmentStatisticsResponseDto> selectTreatmentStatistics(StatisticsSearchRequestDto request);
}
