package com.example.demo.dto.request.schedule;

import java.time.LocalDate;

import lombok.Data;

/**
 * 파일명: AdminStaffScheduleCreateRequestDto.java
 * 설명: 관리자 직원 일정 등록 요청 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-25 | 규민 | 클래스 생성
 */

@Data
public class AdminStaffScheduleCreateRequestDto {
    // 관리자 ID
    private Integer adminId;
    // 일정 날짜
    private LocalDate scheduleDate;
    // 일정 유형
    private String scheduleType;
    // 일정 메모
    private String memo;
}
