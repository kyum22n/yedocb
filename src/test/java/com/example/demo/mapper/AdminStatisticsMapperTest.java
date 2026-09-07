package com.example.demo.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.AbstractIntegrationTest;
import com.example.demo.dao.AdminReservationDao;
import com.example.demo.dao.AdminStatisticsDao;
import com.example.demo.dao.AdminTreatmentDao;
import com.example.demo.dao.UserDao;
import com.example.demo.dto.request.statistics.StatisticsSearchRequestDto;
import com.example.demo.dto.response.statistics.ReservationStatisticsResponseDto;
import com.example.demo.dto.response.statistics.TreatmentStatisticsResponseDto;
import com.example.demo.entity.Reservation;
import com.example.demo.entity.Treatment;
import com.example.demo.entity.User;

/**
 * 파일명: AdminStatisticsMapperTest.java
 * 설명: AdminStatisticsMapper.xml을 실제 Postgres 대상으로 검증한다. Postgres 전용 문법
 *       (ROUND(...::NUMERIC, 2), SUM(CASE WHEN...))이 H2 등 다른 DB가 아니라 실제
 *       Postgres에서 의도한 값을 내는지 확인하는 것이 이 테스트의 핵심 목적이다
 *       (docs/yedoc-migration-plan.md의 "A의 집계 로직 이관" 항목 검증).
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성 — selectTreatmentStatistics의 LEFT JOIN에
 *                        날짜범위 조건이 WHERE절에 있어 사실상 INNER JOIN처럼 동작해 예약이
 *                        없는 진료항목이 통계에서 사라지는 현상을 발견함(아래 테스트, docs/test-report.md 참고)
 */
class AdminStatisticsMapperTest extends AbstractIntegrationTest {

    @Autowired
    private AdminStatisticsDao statisticsDao;

    @Autowired
    private AdminReservationDao adminReservationDao;

    @Autowired
    private AdminTreatmentDao adminTreatmentDao;

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

    private Reservation createReservation(String uId, LocalDate date, String status) {
        Reservation reservation = new Reservation();
        reservation.setUId(uId);
        reservation.setReservationDate(date);
        reservation.setReservationTime(LocalTime.of(10, 0));
        adminReservationDao.insertReservation(reservation);
        if (status != null) {
            reservation.setReservationStatus(status);
            adminReservationDao.updateReservationStatus(reservation);
        }
        return reservation;
    }

    @Test
    void 노쇼율이_Postgres_ROUND연산으로_퍼센트로_계산된다() {
        String uId = createUser("statuser1");
        LocalDate date = LocalDate.of(2026, 1, 10);
        createReservation(uId, date, "NO_SHOW");
        createReservation(uId, date, "CONFIRMED");
        createReservation(uId, date, "CONFIRMED");
        createReservation(uId, date, "CONFIRMED");

        StatisticsSearchRequestDto request = new StatisticsSearchRequestDto();
        request.setStartDate(LocalDate.of(2026, 1, 1));
        request.setEndDate(LocalDate.of(2026, 1, 31));

        ReservationStatisticsResponseDto stats = statisticsDao.selectReservationStatistics(request);

        assertThat(stats.getTotalReservationCount()).isEqualTo(4);
        assertThat(stats.getNoShowCount()).isEqualTo(1);
        assertThat(stats.getNoShowRate()).isEqualTo(25.0);
    }

    @Test
    void 예약이없으면_노쇼율은_0이고_나눗셈오류가나지않는다() {
        StatisticsSearchRequestDto request = new StatisticsSearchRequestDto();
        request.setStartDate(LocalDate.of(2099, 1, 1));
        request.setEndDate(LocalDate.of(2099, 12, 31));

        ReservationStatisticsResponseDto stats = statisticsDao.selectReservationStatistics(request);

        assertThat(stats.getTotalReservationCount()).isEqualTo(0);
        assertThat(stats.getNoShowRate()).isEqualTo(0.0);
    }

    @Test
    void 날짜범위지정시_예약없는진료항목은_LEFT_JOIN이지만_통계에서_빠진다() {
        // 발견된 이슈: WHERE절의 r.reservation_date 조건이 LEFT JOIN을 사실상 INNER
        // JOIN으로 만든다. 날짜 필터 없이 조회하면(reservation_count=0으로) 나와야 할
        // "예약이 아예 없는 진료항목"이, 날짜 필터를 걸면 결과에서 완전히 사라진다.
        Treatment treatment = new Treatment();
        treatment.setTreatmentName("예약없는신규시술");
        adminTreatmentDao.insertTreatment(treatment);

        StatisticsSearchRequestDto noFilter = new StatisticsSearchRequestDto();
        List<TreatmentStatisticsResponseDto> withoutDateFilter = statisticsDao.selectTreatmentStatistics(noFilter);
        assertThat(withoutDateFilter).extracting(TreatmentStatisticsResponseDto::getTreatmentId)
                .contains(treatment.getTreatmentId());

        StatisticsSearchRequestDto withFilter = new StatisticsSearchRequestDto();
        withFilter.setStartDate(LocalDate.of(2026, 1, 1));
        withFilter.setEndDate(LocalDate.of(2026, 12, 31));
        List<TreatmentStatisticsResponseDto> withDateFilter = statisticsDao.selectTreatmentStatistics(withFilter);
        assertThat(withDateFilter).extracting(TreatmentStatisticsResponseDto::getTreatmentId)
                .doesNotContain(treatment.getTreatmentId());
    }
}
