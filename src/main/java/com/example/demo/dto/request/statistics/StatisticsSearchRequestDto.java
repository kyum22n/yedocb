package com.example.demo.dto.request.statistics;

import java.time.LocalDate;

import lombok.Data;

/**
 * 파일명: StatisticsSearchRequestDto.java
 * 설명: 관리자 통계/리포트 조회 조건 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-25 | 규민 | 클래스 생성
 */

@Data
public class StatisticsSearchRequestDto {
    // 조회 시작일
    private LocalDate startDate;
    // 조회 종료일
    private LocalDate endDate;
}
