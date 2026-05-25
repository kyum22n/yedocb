package com.example.demo.dto.response.schedule;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * 파일명: AdminStaffScheduleResponseDto.java
 * 설명: 관리자 직원 일정 응답 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-25 | 규민 | 클래스 생성
 */

@Data
public class AdminStaffScheduleResponseDto {
    // 일정 ID
    private Integer scheduleId;
    // 관리자 ID
    private Integer adminId;
    // 일정 날짜
    private LocalDate scheduleDate;
    // 일정 유형
    private String scheduleType;
    // 일정 메모
    private String memo;
    // 생성일
    private LocalDateTime createdAt;
    // 수정일
    private LocalDateTime updatedAt;
}
