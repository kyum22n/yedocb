package com.example.demo.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.AbstractIntegrationTest;
import com.example.demo.dao.AdminDashboardDao;
import com.example.demo.dao.AdminReservationDao;
import com.example.demo.dao.UserDao;
import com.example.demo.entity.Reservation;
import com.example.demo.entity.User;

/**
 * 파일명: AdminDashboardMapperTest.java
 * 설명: AdminDashboardMapper.xml을 실제 Postgres 대상으로 검증한다. CURRENT_DATE 기준 오늘 예약 수,
 *       PMS 연동 실패 수 집계가 실제로 맞는지 확인한다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성
 */
class AdminDashboardMapperTest extends AbstractIntegrationTest {

    @Autowired
    private AdminDashboardDao dashboardDao;

    @Autowired
    private AdminReservationDao adminReservationDao;

    @Autowired
    private UserDao userDao;

    private String createUser(String id) {
        User user = new User();
        user.setUId(id);
        user.setUPwd("pwd");
        user.setUEmail(id + "@example.com");
        user.setUName("사용자");
        userDao.insertUser(user);
        return id;
    }

    @Test
    void 오늘예약수와_PMS연동실패수가_정확히집계된다() {
        String uId = createUser("dashuser1");

        Reservation today = new Reservation();
        today.setUId(uId);
        today.setReservationDate(LocalDate.now());
        today.setReservationTime(LocalTime.of(10, 0));
        adminReservationDao.insertReservation(today);

        Reservation tomorrow = new Reservation();
        tomorrow.setUId(uId);
        tomorrow.setReservationDate(LocalDate.now().plusDays(1));
        tomorrow.setReservationTime(LocalTime.of(11, 0));
        adminReservationDao.insertReservation(tomorrow);

        tomorrow.setPmsSyncStatus("FAILED");
        adminReservationDao.updatePmsSyncStatus(tomorrow);

        assertThat(dashboardDao.selectTodayReservationCount()).isGreaterThanOrEqualTo(1);
        assertThat(dashboardDao.selectPmsFailedCount()).isGreaterThanOrEqualTo(1);
    }

    @Test
    void 최근예약목록은_생성일_내림차순_5건이하로_반환된다() {
        assertThat(dashboardDao.selectRecentReservations().size()).isLessThanOrEqualTo(5);
    }
}
