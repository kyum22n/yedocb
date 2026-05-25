package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.AdminDashboardDao;
import com.example.demo.dto.response.dashboard.AdminDashboardResponseDto;
import com.example.demo.dto.response.inquiry.AdminInquiryResponseDto;
import com.example.demo.dto.response.reservation.AdminReservationResponseDto;

/**
 * 파일명: AdminDashboardService.java
 * 설명: 관리자용 대시보드 관련 서비스 클래스
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-25 | 규민 | 클래스 생성
 */

@Service
public class AdminDashboardService {

    @Autowired
    private AdminDashboardDao dashboardDao;

    // 대시보드 조회
    public AdminDashboardResponseDto getDashboard() {

        Integer todayReservationCount = dashboardDao.selectTodayReservationCount();
        Integer pendingReservationCount = dashboardDao.selectPendingReservationCount();
        Integer todayConsultationCount = dashboardDao.selectTodayConsultationCount();
        Integer waitingInquiryCount = dashboardDao.selectWaitingInquiryCount();
        Integer pmsFailedCount = dashboardDao.selectPmsFailedCount();
        List<AdminReservationResponseDto> recentReservations = dashboardDao.selectRecentReservations();
        List<AdminInquiryResponseDto> recentInquiries = dashboardDao.selectRecentInquiries();

        AdminDashboardResponseDto response = new AdminDashboardResponseDto();
        response.setTodayReservationCount(todayReservationCount != null ? todayReservationCount : 0);
        response.setPendingReservationCount(pendingReservationCount != null ? pendingReservationCount : 0);
        response.setTodayConsultationCount(todayConsultationCount != null ? todayConsultationCount : 0);
        response.setWaitingInquiryCount(waitingInquiryCount != null ? waitingInquiryCount : 0);
        response.setPmsFailedCount(pmsFailedCount != null ? pmsFailedCount : 0);
        response.setRecentReservations(recentReservations);
        response.setRecentInquiries(recentInquiries);

        return response;
    }
}
