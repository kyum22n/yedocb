package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.statistics.StatisticsSearchRequestDto;
import com.example.demo.dto.response.statistics.AdminStatisticsResponseDto;
import com.example.demo.dto.response.statistics.ConsultationStatisticsResponseDto;
import com.example.demo.dto.response.statistics.InquiryStatisticsResponseDto;
import com.example.demo.dto.response.statistics.ReservationStatisticsResponseDto;
import com.example.demo.dto.response.statistics.TreatmentStatisticsResponseDto;
import com.example.demo.service.AdminStatisticsService;

/**
 * 파일명: AdminStatisticsController.java
 * 설명: 관리자용 통계/리포트 관련 controller
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-25 | 규민 | 클래스 생성
 */

@RestController
@RequestMapping("/admin/statistics")
public class AdminStatisticsController {

    @Autowired
    private AdminStatisticsService statisticsService;

    // 통계/리포트 통합 조회
    @PostMapping("/summary")
    public ResponseEntity<AdminStatisticsResponseDto> getStatistics(@RequestBody StatisticsSearchRequestDto request) {
        return ResponseEntity.ok(statisticsService.getStatistics(request));
    }

    // 예약 통계 조회
    @PostMapping("/reservations")
    public ResponseEntity<ReservationStatisticsResponseDto> getReservationStatistics(
            @RequestBody StatisticsSearchRequestDto request) {
        return ResponseEntity.ok(statisticsService.getReservationStatistics(request));
    }

    // 상담 통계 조회
    @PostMapping("/consultations")
    public ResponseEntity<ConsultationStatisticsResponseDto> getConsultationStatistics(
            @RequestBody StatisticsSearchRequestDto request) {
        return ResponseEntity.ok(statisticsService.getConsultationStatistics(request));
    }

    // 문의 통계 조회
    @PostMapping("/inquiries")
    public ResponseEntity<InquiryStatisticsResponseDto> getInquiryStatistics(
            @RequestBody StatisticsSearchRequestDto request) {
        return ResponseEntity.ok(statisticsService.getInquiryStatistics(request));
    }

    // 인기 진료 항목 통계 조회
    @PostMapping("/treatments")
    public ResponseEntity<List<TreatmentStatisticsResponseDto>> getTreatmentStatistics(
            @RequestBody StatisticsSearchRequestDto request) {
        return ResponseEntity.ok(statisticsService.getTreatmentStatistics(request));
    }
}
