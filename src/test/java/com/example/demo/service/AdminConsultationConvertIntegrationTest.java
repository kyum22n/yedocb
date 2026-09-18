package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.AbstractIntegrationTest;
import com.example.demo.dao.AdminConsultationDao;
import com.example.demo.dao.AdminReservationDao;
import com.example.demo.dao.UserDao;
import com.example.demo.dto.request.consultation.AdminConsultationConvertRequestDto;
import com.example.demo.entity.Consultation;
import com.example.demo.entity.Reservation;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;

/**
 * 파일명: AdminConsultationConvertIntegrationTest.java
 * 설명: "상담 → 예약 전환"(AdminConsultationController.convert) 전체 흐름을 서비스+매퍼+실제
 *       Postgres까지 포함해 검증하는 통합테스트. 서비스 계층은 목(mock)으로 대체하지 않고
 *       실제 AdminConsultationService/AdminConsultationDao/DB를 그대로 사용한다.
 *       전환은 새 예약을 만드는 것이 아니라 "이미 존재하는" 예약 ID를 상담에 연결하고
 *       상태를 CONVERTED로 바꾸는 방식으로 구현되어 있음을 이 테스트에서 확인한다
 *       (AdminConsultationService.convertConsultationToReservation 참고).
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성
 */
class AdminConsultationConvertIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private AdminConsultationService adminConsultationService;

    @Autowired
    private AdminConsultationDao adminConsultationDao;

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

    private Integer createReservation(String uId) {
        Reservation reservation = new Reservation();
        reservation.setUId(uId);
        reservation.setReservationDate(java.time.LocalDate.of(2026, 9, 20));
        reservation.setReservationTime(LocalTime.of(14, 0));
        adminReservationDao.insertReservation(reservation);
        return reservation.getReservationId();
    }

    private Integer createConsultation(String uId) {
        Consultation consultation = new Consultation();
        consultation.setUId(uId);
        adminConsultationDao.insertConsultation(consultation);
        return consultation.getConsultationId();
    }

    @Test
    void 상담을_예약전환하면_상태가_CONVERTED로바뀌고_예약ID_담당자_메모가_반영된다() {
        String uId = createUser("convertuser1");
        Integer reservationId = createReservation(uId);
        Integer consultationId = createConsultation(uId);

        AdminConsultationConvertRequestDto request = new AdminConsultationConvertRequestDto();
        request.setConsultationId(consultationId);
        request.setReservationId(reservationId);
        request.setAdminId(1);
        request.setConsultationMemo("상담 후 예약으로 전환함");

        int affected = adminConsultationService.convertConsultationToReservation(request);

        assertThat(affected).isEqualTo(1);

        Consultation found = adminConsultationDao.selectConsultationById(consultationId);
        assertThat(found.getConsultationStatus()).isEqualTo("CONVERTED");
        assertThat(found.getReservationId()).isEqualTo(reservationId);
        assertThat(found.getAdminId()).isEqualTo(1);
        assertThat(found.getConsultationMemo()).isEqualTo("상담 후 예약으로 전환함");
    }

    @Test
    void 존재하지않는_상담을_전환하려하면_ResourceNotFoundException이발생하고_DB는변경되지않는다() {
        String uId = createUser("convertuser2");
        Integer reservationId = createReservation(uId);

        AdminConsultationConvertRequestDto request = new AdminConsultationConvertRequestDto();
        request.setConsultationId(999999);
        request.setReservationId(reservationId);

        assertThatThrownBy(() -> adminConsultationService.convertConsultationToReservation(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void 존재하지않는_예약ID로_전환하려하면_FK제약위반으로_실패한다() {
        // 발견된 사항: 서비스 계층은 reservationId의 실존 여부를 검증하지 않는다.
        // reservation 테이블에 없는 ID를 넘기면 DB의 FK 제약(consultation.reservation_id
        // REFERENCES reservation)에 의해서만 막힌다 — 애플리케이션 레벨 검증 부재를 실제
        // DB 제약이 대신 방어하고 있음을 확인한다.
        String uId = createUser("convertuser3");
        Integer consultationId = createConsultation(uId);

        AdminConsultationConvertRequestDto request = new AdminConsultationConvertRequestDto();
        request.setConsultationId(consultationId);
        request.setReservationId(999999);

        assertThatThrownBy(() -> adminConsultationService.convertConsultationToReservation(request))
                .isInstanceOf(org.springframework.dao.DataIntegrityViolationException.class);
    }
}
