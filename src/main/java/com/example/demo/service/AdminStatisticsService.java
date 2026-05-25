package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.AdminStatisticsDao;
import com.example.demo.dto.request.statistics.StatisticsSearchRequestDto;
import com.example.demo.dto.response.statistics.AdminStatisticsResponseDto;
import com.example.demo.dto.response.statistics.ConsultationStatisticsResponseDto;
import com.example.demo.dto.response.statistics.InquiryStatisticsResponseDto;
import com.example.demo.dto.response.statistics.ReservationStatisticsResponseDto;
import com.example.demo.dto.response.statistics.TreatmentStatisticsResponseDto;

/**
 * 파일명: AdminStatisticsService.java
 * 설명: 관리자용 통계/리포트 관련 서비스 클래스
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-25 | 규민 | 클래스 생성
 */

@Service
public class AdminStatisticsService {

    @Autowired
    private AdminStatisticsDao statisticsDao;

    // 통계/리포트 통합 조회
    public AdminStatisticsResponseDto getStatistics(StatisticsSearchRequestDto request) {

        if(request == null) {
            request = new StatisticsSearchRequestDto();
        }

        if(request.getStartDate() != null && request.getEndDate() != null && request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("조회 종료일은 시작일보다 빠를 수 없습니다.");
        }

        ReservationStatisticsResponseDto reservationStatistics = statisticsDao.selectReservationStatistics(request);
        if(reservationStatistics == null) {
            reservationStatistics = new ReservationStatisticsResponseDto();
        }

        if(reservationStatistics.getTotalReservationCount() == null) {
            reservationStatistics.setTotalReservationCount(0);
        }
        if(reservationStatistics.getPendingCount() == null) {
            reservationStatistics.setPendingCount(0);
        }
        if(reservationStatistics.getConfirmedCount() == null) {
            reservationStatistics.setConfirmedCount(0);
        }
        if(reservationStatistics.getCompletedCount() == null) {
            reservationStatistics.setCompletedCount(0);
        }
        if(reservationStatistics.getCanceledCount() == null) {
            reservationStatistics.setCanceledCount(0);
        }
        if(reservationStatistics.getNoShowCount() == null) {
            reservationStatistics.setNoShowCount(0);
        }

        if(reservationStatistics.getTotalReservationCount() == 0) {
            reservationStatistics.setNoShowRate(0.0);
        } else {
            double noShowRate = reservationStatistics.getNoShowCount() * 100.0 / reservationStatistics.getTotalReservationCount();
            reservationStatistics.setNoShowRate(Math.round(noShowRate * 100.0) / 100.0);
        }

        ConsultationStatisticsResponseDto consultationStatistics = statisticsDao.selectConsultationStatistics(request);
        if(consultationStatistics == null) {
            consultationStatistics = new ConsultationStatisticsResponseDto();
        }

        if(consultationStatistics.getTotalConsultationCount() == null) {
            consultationStatistics.setTotalConsultationCount(0);
        }
        if(consultationStatistics.getReceivedCount() == null) {
            consultationStatistics.setReceivedCount(0);
        }
        if(consultationStatistics.getScheduledCount() == null) {
            consultationStatistics.setScheduledCount(0);
        }
        if(consultationStatistics.getCompletedCount() == null) {
            consultationStatistics.setCompletedCount(0);
        }
        if(consultationStatistics.getConvertedCount() == null) {
            consultationStatistics.setConvertedCount(0);
        }
        if(consultationStatistics.getCanceledCount() == null) {
            consultationStatistics.setCanceledCount(0);
        }

        if(consultationStatistics.getTotalConsultationCount() == 0) {
            consultationStatistics.setConversionRate(0.0);
        } else {
            double conversionRate = consultationStatistics.getConvertedCount() * 100.0 / consultationStatistics.getTotalConsultationCount();
            consultationStatistics.setConversionRate(Math.round(conversionRate * 100.0) / 100.0);
        }

        InquiryStatisticsResponseDto inquiryStatistics = statisticsDao.selectInquiryStatistics(request);
        if(inquiryStatistics == null) {
            inquiryStatistics = new InquiryStatisticsResponseDto();
        }

        if(inquiryStatistics.getTotalInquiryCount() == null) {
            inquiryStatistics.setTotalInquiryCount(0);
        }
        if(inquiryStatistics.getWaitingCount() == null) {
            inquiryStatistics.setWaitingCount(0);
        }
        if(inquiryStatistics.getAnsweredCount() == null) {
            inquiryStatistics.setAnsweredCount(0);
        }

        List<TreatmentStatisticsResponseDto> treatmentStatistics = statisticsDao.selectTreatmentStatistics(request);

        AdminStatisticsResponseDto response = new AdminStatisticsResponseDto();
        response.setReservationStatistics(reservationStatistics);
        response.setConsultationStatistics(consultationStatistics);
        response.setInquiryStatistics(inquiryStatistics);
        response.setTreatmentStatistics(treatmentStatistics);

        return response;
    }

    // 예약 통계 조회
    public ReservationStatisticsResponseDto getReservationStatistics(StatisticsSearchRequestDto request) {

        if(request == null) {
            request = new StatisticsSearchRequestDto();
        }

        if(request.getStartDate() != null && request.getEndDate() != null && request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("조회 종료일은 시작일보다 빠를 수 없습니다.");
        }

        ReservationStatisticsResponseDto response = statisticsDao.selectReservationStatistics(request);

        if(response == null) {
            response = new ReservationStatisticsResponseDto();
        }

        if(response.getTotalReservationCount() == null) {
            response.setTotalReservationCount(0);
        }
        if(response.getPendingCount() == null) {
            response.setPendingCount(0);
        }
        if(response.getConfirmedCount() == null) {
            response.setConfirmedCount(0);
        }
        if(response.getCompletedCount() == null) {
            response.setCompletedCount(0);
        }
        if(response.getCanceledCount() == null) {
            response.setCanceledCount(0);
        }
        if(response.getNoShowCount() == null) {
            response.setNoShowCount(0);
        }

        if(response.getTotalReservationCount() == 0) {
            response.setNoShowRate(0.0);
        } else {
            double noShowRate = response.getNoShowCount() * 100.0 / response.getTotalReservationCount();
            response.setNoShowRate(Math.round(noShowRate * 100.0) / 100.0);
        }

        return response;
    }

    // 상담 통계 조회
    public ConsultationStatisticsResponseDto getConsultationStatistics(StatisticsSearchRequestDto request) {

        if(request == null) {
            request = new StatisticsSearchRequestDto();
        }

        if(request.getStartDate() != null && request.getEndDate() != null && request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("조회 종료일은 시작일보다 빠를 수 없습니다.");
        }

        ConsultationStatisticsResponseDto response = statisticsDao.selectConsultationStatistics(request);

        if(response == null) {
            response = new ConsultationStatisticsResponseDto();
        }

        if(response.getTotalConsultationCount() == null) {
            response.setTotalConsultationCount(0);
        }
        if(response.getReceivedCount() == null) {
            response.setReceivedCount(0);
        }
        if(response.getScheduledCount() == null) {
            response.setScheduledCount(0);
        }
        if(response.getCompletedCount() == null) {
            response.setCompletedCount(0);
        }
        if(response.getConvertedCount() == null) {
            response.setConvertedCount(0);
        }
        if(response.getCanceledCount() == null) {
            response.setCanceledCount(0);
        }

        if(response.getTotalConsultationCount() == 0) {
            response.setConversionRate(0.0);
        } else {
            double conversionRate = response.getConvertedCount() * 100.0 / response.getTotalConsultationCount();
            response.setConversionRate(Math.round(conversionRate * 100.0) / 100.0);
        }

        return response;
    }

    // 문의 통계 조회
    public InquiryStatisticsResponseDto getInquiryStatistics(StatisticsSearchRequestDto request) {

        if(request == null) {
            request = new StatisticsSearchRequestDto();
        }

        if(request.getStartDate() != null && request.getEndDate() != null && request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("조회 종료일은 시작일보다 빠를 수 없습니다.");
        }

        InquiryStatisticsResponseDto response = statisticsDao.selectInquiryStatistics(request);

        if(response == null) {
            response = new InquiryStatisticsResponseDto();
        }

        if(response.getTotalInquiryCount() == null) {
            response.setTotalInquiryCount(0);
        }
        if(response.getWaitingCount() == null) {
            response.setWaitingCount(0);
        }
        if(response.getAnsweredCount() == null) {
            response.setAnsweredCount(0);
        }

        return response;
    }

    // 인기 진료 항목 통계 조회
    public List<TreatmentStatisticsResponseDto> getTreatmentStatistics(StatisticsSearchRequestDto request) {

        if(request == null) {
            request = new StatisticsSearchRequestDto();
        }

        if(request.getStartDate() != null && request.getEndDate() != null && request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("조회 종료일은 시작일보다 빠를 수 없습니다.");
        }

        return statisticsDao.selectTreatmentStatistics(request);
    }
}
