package com.example.demo.dto.request.schedule;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 파일명: AdminStaffScheduleUpdateRequestDto.java
 * 설명: 관리자 직원 일정 수정 요청 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-25 | 규민 | 클래스 생성
 */

@Data
public class AdminStaffScheduleUpdateRequestDto {
    // 일정 ID
    @NotNull
    private Integer scheduleId;
    // 관리자 ID
    @NotNull
    private Integer adminId;
    // 일정 날짜
    @NotNull
    private LocalDate scheduleDate;
    // 일정 유형
    @NotBlank
    private String scheduleType;
    // 일정 메모
    private String memo;
}
