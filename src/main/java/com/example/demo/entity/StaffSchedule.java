package com.example.demo.entity;

import lombok.Data;

/**
 * 파일명: StaffSchedule.java
 * 설명: 직원 근무 일정 관련 엔티티
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 엔티티 추가
 */

@Data
public class StaffSchedule {
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
