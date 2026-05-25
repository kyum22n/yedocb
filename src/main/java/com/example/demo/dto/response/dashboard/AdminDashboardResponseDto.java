package com.example.demo.dto.response.dashboard;

import java.util.List;

import com.example.demo.dto.response.inquiry.AdminInquiryResponseDto;
import com.example.demo.dto.response.reservation.AdminReservationResponseDto;

import lombok.Data;

/**
 * 파일명: AdminDashboardResponseDto.java
 * 설명: 관리자 대시보드 응답 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-25 | 규민 | 클래스 생성
 */

@Data
public class AdminDashboardResponseDto {
    // 오늘 예약 수
    private Integer todayReservationCount;
    // 대기 예약 수
    private Integer pendingReservationCount;
    // 오늘 상담 수
    private Integer todayConsultationCount;
    // 답변 대기 문의 수
    private Integer waitingInquiryCount;
    // PMS 연동 실패 수
    private Integer pmsFailedCount;
    // 최근 예약 목록
    private List<AdminReservationResponseDto> recentReservations;
    // 최근 문의 목록
    private List<AdminInquiryResponseDto> recentInquiries;
}
