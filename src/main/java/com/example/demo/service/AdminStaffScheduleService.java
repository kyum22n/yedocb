package com.example.demo.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.AdminStaffScheduleDao;
import com.example.demo.dto.request.schedule.AdminStaffScheduleCreateRequestDto;
import com.example.demo.dto.request.schedule.AdminStaffScheduleUpdateRequestDto;
import com.example.demo.dto.response.schedule.AdminStaffScheduleResponseDto;
import com.example.demo.entity.StaffSchedule;

/**
 * 파일명: AdminStaffScheduleService.java
 * 설명: 관리자용 직원 일정 관련 서비스 클래스
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-25 | 규민 | 클래스 생성
 */

@Service
public class AdminStaffScheduleService {

    @Autowired
    private AdminStaffScheduleDao staffScheduleDao;

    // 직원 일정 등록
    public int createStaffSchedule(AdminStaffScheduleCreateRequestDto request) {

        if(request == null) {
            throw new IllegalArgumentException("직원 일정 등록 요청 정보가 없습니다.");
        }

        if(request.getAdminId() == null) {
            throw new IllegalArgumentException("관리자 ID는 필수입니다.");
        }

        if(request.getScheduleDate() == null) {
            throw new IllegalArgumentException("일정 날짜는 필수입니다.");
        }

        if(request.getScheduleType() == null || request.getScheduleType().isBlank()) {
            throw new IllegalArgumentException("일정 유형은 필수입니다.");
        }

        if(!request.getScheduleType().equals("WORK") && !request.getScheduleType().equals("OFF")) {
            throw new IllegalArgumentException("올바르지 않은 일정 유형입니다.");
        }

        StaffSchedule staffSchedule = new StaffSchedule();
        staffSchedule.setAdminId(request.getAdminId());
        staffSchedule.setScheduleDate(request.getScheduleDate());
        staffSchedule.setScheduleType(request.getScheduleType());
        staffSchedule.setMemo(request.getMemo());

        return staffScheduleDao.insertStaffSchedule(staffSchedule);
    }

    // 직원 일정 목록 조회
    public List<AdminStaffScheduleResponseDto> getAllStaffSchedules() {

        List<StaffSchedule> schedules = staffScheduleDao.selectAllStaffSchedules();
        List<AdminStaffScheduleResponseDto> listResponse = new ArrayList<>();

        for(StaffSchedule schedule : schedules) {
            AdminStaffScheduleResponseDto response = new AdminStaffScheduleResponseDto();
            response.setScheduleId(schedule.getScheduleId());
            response.setAdminId(schedule.getAdminId());
            response.setScheduleDate(schedule.getScheduleDate());
            response.setScheduleType(schedule.getScheduleType());
            response.setMemo(schedule.getMemo());
            response.setCreatedAt(schedule.getCreatedAt());
            response.setUpdatedAt(schedule.getUpdatedAt());

            listResponse.add(response);
        }

        return listResponse;
    }

    // 관리자별 직원 일정 목록 조회
    public List<AdminStaffScheduleResponseDto> getStaffSchedulesByAdminId(Integer adminId) {

        if(adminId == null) {
            throw new IllegalArgumentException("관리자 ID는 필수입니다.");
        }

        List<StaffSchedule> schedules = staffScheduleDao.selectStaffSchedulesByAdminId(adminId);
        List<AdminStaffScheduleResponseDto> listResponse = new ArrayList<>();

        for(StaffSchedule schedule : schedules) {
            AdminStaffScheduleResponseDto response = new AdminStaffScheduleResponseDto();
            response.setScheduleId(schedule.getScheduleId());
            response.setAdminId(schedule.getAdminId());
            response.setScheduleDate(schedule.getScheduleDate());
            response.setScheduleType(schedule.getScheduleType());
            response.setMemo(schedule.getMemo());
            response.setCreatedAt(schedule.getCreatedAt());
            response.setUpdatedAt(schedule.getUpdatedAt());

            listResponse.add(response);
        }

        return listResponse;
    }

    // 날짜별 직원 일정 목록 조회
    public List<AdminStaffScheduleResponseDto> getStaffSchedulesByDate(LocalDate scheduleDate) {

        if(scheduleDate == null) {
            throw new IllegalArgumentException("일정 날짜는 필수입니다.");
        }

        List<StaffSchedule> schedules = staffScheduleDao.selectStaffSchedulesByDate(scheduleDate);
        List<AdminStaffScheduleResponseDto> listResponse = new ArrayList<>();

        for(StaffSchedule schedule : schedules) {
            AdminStaffScheduleResponseDto response = new AdminStaffScheduleResponseDto();
            response.setScheduleId(schedule.getScheduleId());
            response.setAdminId(schedule.getAdminId());
            response.setScheduleDate(schedule.getScheduleDate());
            response.setScheduleType(schedule.getScheduleType());
            response.setMemo(schedule.getMemo());
            response.setCreatedAt(schedule.getCreatedAt());
            response.setUpdatedAt(schedule.getUpdatedAt());

            listResponse.add(response);
        }

        return listResponse;
    }

    // 일정 유형별 직원 일정 목록 조회
    public List<AdminStaffScheduleResponseDto> getStaffSchedulesByType(String scheduleType) {

        if(scheduleType == null || scheduleType.isBlank()) {
            throw new IllegalArgumentException("일정 유형은 필수입니다.");
        }

        if(!scheduleType.equals("WORK") && !scheduleType.equals("OFF")) {
            throw new IllegalArgumentException("올바르지 않은 일정 유형입니다.");
        }

        List<StaffSchedule> schedules = staffScheduleDao.selectStaffSchedulesByType(scheduleType);
        List<AdminStaffScheduleResponseDto> listResponse = new ArrayList<>();

        for(StaffSchedule schedule : schedules) {
            AdminStaffScheduleResponseDto response = new AdminStaffScheduleResponseDto();
            response.setScheduleId(schedule.getScheduleId());
            response.setAdminId(schedule.getAdminId());
            response.setScheduleDate(schedule.getScheduleDate());
            response.setScheduleType(schedule.getScheduleType());
            response.setMemo(schedule.getMemo());
            response.setCreatedAt(schedule.getCreatedAt());
            response.setUpdatedAt(schedule.getUpdatedAt());

            listResponse.add(response);
        }

        return listResponse;
    }

    // 직원 일정 상세 조회
    public AdminStaffScheduleResponseDto getStaffScheduleById(Integer scheduleId) {

        if(scheduleId == null) {
            throw new IllegalArgumentException("일정 ID는 필수입니다.");
        }

        StaffSchedule schedule = staffScheduleDao.selectStaffScheduleById(scheduleId);

        if(schedule == null) {
            throw new IllegalArgumentException("존재하지 않는 직원 일정입니다.");
        }

        AdminStaffScheduleResponseDto response = new AdminStaffScheduleResponseDto();
        response.setScheduleId(schedule.getScheduleId());
        response.setAdminId(schedule.getAdminId());
        response.setScheduleDate(schedule.getScheduleDate());
        response.setScheduleType(schedule.getScheduleType());
        response.setMemo(schedule.getMemo());
        response.setCreatedAt(schedule.getCreatedAt());
        response.setUpdatedAt(schedule.getUpdatedAt());

        return response;
    }

    // 직원 일정 수정
    public int modifyStaffSchedule(AdminStaffScheduleUpdateRequestDto request) {

        if(request == null) {
            throw new IllegalArgumentException("직원 일정 수정 요청 정보가 없습니다.");
        }

        if(request.getScheduleId() == null) {
            throw new IllegalArgumentException("일정 ID는 필수입니다.");
        }

        if(request.getAdminId() == null) {
            throw new IllegalArgumentException("관리자 ID는 필수입니다.");
        }

        if(request.getScheduleDate() == null) {
            throw new IllegalArgumentException("일정 날짜는 필수입니다.");
        }

        if(request.getScheduleType() == null || request.getScheduleType().isBlank()) {
            throw new IllegalArgumentException("일정 유형은 필수입니다.");
        }

        if(!request.getScheduleType().equals("WORK") && !request.getScheduleType().equals("OFF")) {
            throw new IllegalArgumentException("올바르지 않은 일정 유형입니다.");
        }

        StaffSchedule existingSchedule = staffScheduleDao.selectStaffScheduleById(request.getScheduleId());

        if(existingSchedule == null) {
            throw new IllegalArgumentException("존재하지 않는 직원 일정입니다.");
        }

        StaffSchedule staffSchedule = new StaffSchedule();
        staffSchedule.setScheduleId(request.getScheduleId());
        staffSchedule.setAdminId(request.getAdminId());
        staffSchedule.setScheduleDate(request.getScheduleDate());
        staffSchedule.setScheduleType(request.getScheduleType());
        staffSchedule.setMemo(request.getMemo());

        return staffScheduleDao.updateStaffSchedule(staffSchedule);
    }

    // 직원 일정 삭제
    public int removeStaffSchedule(Integer scheduleId) {

        if(scheduleId == null) {
            throw new IllegalArgumentException("일정 ID는 필수입니다.");
        }

        StaffSchedule existingSchedule = staffScheduleDao.selectStaffScheduleById(scheduleId);

        if(existingSchedule == null) {
            throw new IllegalArgumentException("존재하지 않는 직원 일정입니다.");
        }

        return staffScheduleDao.deleteStaffSchedule(scheduleId);
    }
}
