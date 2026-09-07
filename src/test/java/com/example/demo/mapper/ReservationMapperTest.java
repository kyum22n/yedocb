package com.example.demo.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.AbstractIntegrationTest;
import com.example.demo.dao.AdminReservationDao;
import com.example.demo.dao.ReservationDao;
import com.example.demo.dao.UserDao;
import com.example.demo.entity.Reservation;
import com.example.demo.entity.User;

/**
 * 파일명: ReservationMapperTest.java
 * 설명: ReservationMapper.xml / AdminReservationMapper.xml을 실제 Postgres 대상으로 검증한다.
 *       예약 등록이 row count가 아니라 생성된 PK(useGeneratedKeys)를 반환하는지, 예약 마감
 *       시간대·중복 예약 검사 쿼리가 취소/노쇼 상태를 올바르게 제외하는지 확인한다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성
 */
class ReservationMapperTest extends AbstractIntegrationTest {

    @Autowired
    private ReservationDao reservationDao;

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

    private Reservation newReservation(String uId, LocalDate date, LocalTime time) {
        Reservation reservation = new Reservation();
        reservation.setUId(uId);
        reservation.setReservationDate(date);
        reservation.setReservationTime(time);
        return reservation;
    }

    @Test
    void 예약등록하면_생성된PK가_엔티티에_채워진다() {
        String uId = createUser("resvuser1");
        Reservation reservation = newReservation(uId, LocalDate.of(2026, 9, 10), LocalTime.of(10, 0));

        reservationDao.insertReservation(reservation);

        assertThat(reservation.getReservationId()).isNotNull();
    }

    @Test
    void 마감시간대조회는_취소_노쇼상태를_제외한다() {
        String uId = createUser("resvuser2");
        LocalDate date = LocalDate.of(2026, 9, 11);

        Reservation active = newReservation(uId, date, LocalTime.of(10, 0));
        reservationDao.insertReservation(active);

        Reservation canceled = newReservation(uId, date, LocalTime.of(11, 0));
        reservationDao.insertReservation(canceled);
        reservationDao.cancelReservation(canceled.getReservationId(), uId);

        List<LocalTime> disabledTimes = reservationDao.selectReservedTimesByDate(date);

        assertThat(disabledTimes).containsExactly(LocalTime.of(10, 0));
    }

    @Test
    void 동일시간대에_이미_예약이있으면_중복으로_감지한다() {
        String uId = createUser("resvuser3");
        LocalDate date = LocalDate.of(2026, 9, 12);
        LocalTime time = LocalTime.of(14, 0);

        Reservation reservation = newReservation(uId, date, time);
        reservationDao.insertReservation(reservation);

        boolean conflict = reservationDao.existsConflictingReservation(date, time, null);
        assertThat(conflict).isTrue();

        boolean noConflictWhenExcludingSelf =
                reservationDao.existsConflictingReservation(date, time, reservation.getReservationId());
        assertThat(noConflictWhenExcludingSelf).isFalse();
    }

    @Test
    void 관리자용_상태별_예약조회가_동작한다() {
        String uId = createUser("resvuser4");
        Reservation reservation = newReservation(uId, LocalDate.of(2026, 9, 13), LocalTime.of(9, 0));
        reservationDao.insertReservation(reservation);

        List<Reservation> pending = adminReservationDao.selectReservationsByStatus("PENDING");

        assertThat(pending).extracting(Reservation::getReservationId).contains(reservation.getReservationId());
    }
}
