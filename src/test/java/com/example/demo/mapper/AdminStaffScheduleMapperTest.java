package com.example.demo.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.AbstractIntegrationTest;
import com.example.demo.dao.AdminStaffScheduleDao;
import com.example.demo.entity.StaffSchedule;

/**
 * 파일명: AdminStaffScheduleMapperTest.java
 * 설명: AdminStaffScheduleMapper.xml을 실제 Postgres 대상으로 검증한다. 중복 일정 검사에
 *       쓰이는 selectStaffScheduleByAdminIdAndDate가 동일 관리자/날짜 조합만 정확히 찾아내는지
 *       (다른 관리자·다른 날짜는 걸리지 않아야 함) 확인한다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성
 */
class AdminStaffScheduleMapperTest extends AbstractIntegrationTest {

    @Autowired
    private AdminStaffScheduleDao staffScheduleDao;

    private StaffSchedule newSchedule(Integer adminId, LocalDate date, String type) {
        StaffSchedule schedule = new StaffSchedule();
        schedule.setAdminId(adminId);
        schedule.setScheduleDate(date);
        schedule.setScheduleType(type);
        return schedule;
    }

    @Test
    void 동일관리자_동일날짜조회는_등록한일정을_찾는다() {
        LocalDate date = LocalDate.of(2026, 9, 15);
        staffScheduleDao.insertStaffSchedule(newSchedule(1, date, "WORK"));

        StaffSchedule found = staffScheduleDao.selectStaffScheduleByAdminIdAndDate(1, date);

        assertThat(found).isNotNull();
        assertThat(found.getScheduleType()).isEqualTo("WORK");
    }

    @Test
    void 다른관리자_같은날짜는_중복으로_잡히지않는다() {
        LocalDate date = LocalDate.of(2026, 9, 16);
        staffScheduleDao.insertStaffSchedule(newSchedule(1, date, "WORK"));

        StaffSchedule found = staffScheduleDao.selectStaffScheduleByAdminIdAndDate(2, date);

        assertThat(found).isNull();
    }

    @Test
    void 같은관리자_다른날짜는_중복으로_잡히지않는다() {
        staffScheduleDao.insertStaffSchedule(newSchedule(3, LocalDate.of(2026, 9, 17), "WORK"));

        StaffSchedule found = staffScheduleDao.selectStaffScheduleByAdminIdAndDate(3, LocalDate.of(2026, 9, 18));

        assertThat(found).isNull();
    }

    @Test
    void 일정목록_수정_삭제가_동작한다() {
        StaffSchedule schedule = newSchedule(4, LocalDate.of(2026, 9, 19), "WORK");
        staffScheduleDao.insertStaffSchedule(schedule);

        schedule.setScheduleType("DAY_OFF");
        staffScheduleDao.updateStaffSchedule(schedule);
        assertThat(staffScheduleDao.selectStaffScheduleById(schedule.getScheduleId()).getScheduleType())
                .isEqualTo("DAY_OFF");

        List<StaffSchedule> byType = staffScheduleDao.selectStaffSchedulesByType("DAY_OFF");
        assertThat(byType).extracting(StaffSchedule::getScheduleId).contains(schedule.getScheduleId());

        staffScheduleDao.deleteStaffSchedule(schedule.getScheduleId());
        assertThat(staffScheduleDao.selectStaffScheduleById(schedule.getScheduleId())).isNull();
    }
}
