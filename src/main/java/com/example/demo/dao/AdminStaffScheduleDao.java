package com.example.demo.dao;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.entity.StaffSchedule;

/**
 * 파일명: AdminStaffScheduleDao.java
 * 설명: 관리자용 직원 일정 관련 DAO 인터페이스
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-25 | 규민 | 인터페이스 생성
 */

@Mapper
public interface AdminStaffScheduleDao {

    // 직원 일정 등록
    public int insertStaffSchedule(StaffSchedule staffSchedule);

    // 직원 일정 목록 조회
    public List<StaffSchedule> selectAllStaffSchedules();

    // 관리자별 직원 일정 목록 조회
    public List<StaffSchedule> selectStaffSchedulesByAdminId(Integer adminId);

    // 날짜별 직원 일정 목록 조회
    public List<StaffSchedule> selectStaffSchedulesByDate(LocalDate scheduleDate);

    // 일정 유형별 직원 일정 목록 조회
    public List<StaffSchedule> selectStaffSchedulesByType(String scheduleType);

    // 직원 일정 상세 조회
    public StaffSchedule selectStaffScheduleById(Integer scheduleId);

    // 직원 일정 수정
    public int updateStaffSchedule(StaffSchedule staffSchedule);

    // 직원 일정 삭제
    public int deleteStaffSchedule(Integer scheduleId);
}
