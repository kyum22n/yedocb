package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.dao.AdminStaffScheduleDao;
import com.example.demo.dto.request.schedule.AdminStaffScheduleCreateRequestDto;
import com.example.demo.dto.request.schedule.AdminStaffScheduleUpdateRequestDto;
import com.example.demo.entity.StaffSchedule;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.ResourceNotFoundException;

/**
 * 파일명: AdminStaffScheduleServiceTest.java
 * 설명: AdminStaffScheduleService 단위 테스트 (Mockito, 실제 DB 미사용)
 *       — 동일 관리자+날짜 중복 일정 등록 방지 버그 수정에 대한 회귀 테스트 포함
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | 중복 일정 검증 버그 수정에 대한 단위 테스트 작성 (Phase 2)
 */
@ExtendWith(MockitoExtension.class)
class AdminStaffScheduleServiceTest {

    @Mock
    private AdminStaffScheduleDao staffScheduleDao;

    @InjectMocks
    private AdminStaffScheduleService staffScheduleService;

    private AdminStaffScheduleCreateRequestDto createRequest;

    @BeforeEach
    void setUp() {
        createRequest = new AdminStaffScheduleCreateRequestDto();
        createRequest.setAdminId(1);
        createRequest.setScheduleDate(LocalDate.of(2026, 9, 10));
        createRequest.setScheduleType("WORK");
        createRequest.setMemo("오전 근무");
    }

    @Test
    void 일정등록_중복없으면_성공() {
        when(staffScheduleDao.selectStaffScheduleByAdminIdAndDate(1, LocalDate.of(2026, 9, 10)))
                .thenReturn(null);
        when(staffScheduleDao.insertStaffSchedule(any(StaffSchedule.class))).thenReturn(1);

        int result = staffScheduleService.createStaffSchedule(createRequest);

        assertThat(result).isEqualTo(1);
    }

    @Test
    void 일정등록_동일관리자동일날짜_중복이면_DuplicateResourceException() {
        StaffSchedule existing = new StaffSchedule();
        existing.setScheduleId(100);
        existing.setAdminId(1);
        existing.setScheduleDate(LocalDate.of(2026, 9, 10));

        when(staffScheduleDao.selectStaffScheduleByAdminIdAndDate(1, LocalDate.of(2026, 9, 10)))
                .thenReturn(existing);

        assertThatThrownBy(() -> staffScheduleService.createStaffSchedule(createRequest))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void 일정수정_다른일정과_동일관리자동일날짜로_중복이면_DuplicateResourceException() {
        StaffSchedule targetSchedule = new StaffSchedule();
        targetSchedule.setScheduleId(1);
        targetSchedule.setAdminId(1);
        targetSchedule.setScheduleDate(LocalDate.of(2026, 9, 1));

        StaffSchedule conflictingSchedule = new StaffSchedule();
        conflictingSchedule.setScheduleId(2);
        conflictingSchedule.setAdminId(1);
        conflictingSchedule.setScheduleDate(LocalDate.of(2026, 9, 10));

        AdminStaffScheduleUpdateRequestDto updateRequest = new AdminStaffScheduleUpdateRequestDto();
        updateRequest.setScheduleId(1);
        updateRequest.setAdminId(1);
        updateRequest.setScheduleDate(LocalDate.of(2026, 9, 10));
        updateRequest.setScheduleType("WORK");

        when(staffScheduleDao.selectStaffScheduleById(1)).thenReturn(targetSchedule);
        when(staffScheduleDao.selectStaffScheduleByAdminIdAndDate(1, LocalDate.of(2026, 9, 10)))
                .thenReturn(conflictingSchedule);

        assertThatThrownBy(() -> staffScheduleService.modifyStaffSchedule(updateRequest))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void 일정수정_자기자신의날짜를그대로수정하면_중복으로판단하지않는다() {
        StaffSchedule targetSchedule = new StaffSchedule();
        targetSchedule.setScheduleId(1);
        targetSchedule.setAdminId(1);
        targetSchedule.setScheduleDate(LocalDate.of(2026, 9, 10));

        AdminStaffScheduleUpdateRequestDto updateRequest = new AdminStaffScheduleUpdateRequestDto();
        updateRequest.setScheduleId(1);
        updateRequest.setAdminId(1);
        updateRequest.setScheduleDate(LocalDate.of(2026, 9, 10));
        updateRequest.setScheduleType("OFF");

        when(staffScheduleDao.selectStaffScheduleById(1)).thenReturn(targetSchedule);
        when(staffScheduleDao.selectStaffScheduleByAdminIdAndDate(1, LocalDate.of(2026, 9, 10)))
                .thenReturn(targetSchedule);
        when(staffScheduleDao.updateStaffSchedule(any(StaffSchedule.class))).thenReturn(1);

        int result = staffScheduleService.modifyStaffSchedule(updateRequest);

        assertThat(result).isEqualTo(1);
    }

    @Test
    void 일정수정_존재하지않으면_ResourceNotFoundException() {
        AdminStaffScheduleUpdateRequestDto updateRequest = new AdminStaffScheduleUpdateRequestDto();
        updateRequest.setScheduleId(999);
        updateRequest.setAdminId(1);
        updateRequest.setScheduleDate(LocalDate.of(2026, 9, 10));
        updateRequest.setScheduleType("WORK");

        when(staffScheduleDao.selectStaffScheduleById(999)).thenReturn(null);

        assertThatThrownBy(() -> staffScheduleService.modifyStaffSchedule(updateRequest))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void 일정삭제_존재하지않으면_ResourceNotFoundException() {
        when(staffScheduleDao.selectStaffScheduleById(anyInt())).thenReturn(null);

        assertThatThrownBy(() -> staffScheduleService.removeStaffSchedule(999))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
