package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.dao.AdminReservationDao;
import com.example.demo.dto.request.reservation.AdminReservationStatusUpdateRequestDto;
import com.example.demo.entity.Reservation;
import com.example.demo.exception.ResourceNotFoundException;

/**
 * 파일명: AdminReservationServiceTest.java
 * 설명: AdminReservationService 단위 테스트 (Mockito, 실제 DB 미사용) —
 *       예약 상태 전이 검증 로직(2026-09-17 추가)을 중심으로 확인한다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-17 | 디버깅 | 상태 전이 검증 테스트 신규 작성
 */
@ExtendWith(MockitoExtension.class)
class AdminReservationServiceTest {

    @Mock
    private AdminReservationDao adminReservationDao;

    @InjectMocks
    private AdminReservationService adminReservationService;

    private Reservation existingReservationWithStatus(String status) {
        Reservation reservation = new Reservation();
        reservation.setReservationId(1);
        reservation.setReservationStatus(status);
        return reservation;
    }

    private AdminReservationStatusUpdateRequestDto request(Integer reservationId, String status) {
        AdminReservationStatusUpdateRequestDto request = new AdminReservationStatusUpdateRequestDto();
        request.setReservationId(reservationId);
        request.setReservationStatus(status);
        return request;
    }

    @Test
    void PENDING에서_CONFIRMED로_전이하면_성공한다() {
        when(adminReservationDao.selectReservationById(1)).thenReturn(existingReservationWithStatus("PENDING"));
        when(adminReservationDao.updateReservationStatus(any(Reservation.class))).thenReturn(1);

        int result = adminReservationService.modifyReservationStatus(request(1, "CONFIRMED"));

        assertThat(result).isEqualTo(1);
    }

    @Test
    void CONFIRMED에서_COMPLETED로_전이하면_성공한다() {
        when(adminReservationDao.selectReservationById(1)).thenReturn(existingReservationWithStatus("CONFIRMED"));
        when(adminReservationDao.updateReservationStatus(any(Reservation.class))).thenReturn(1);

        int result = adminReservationService.modifyReservationStatus(request(1, "COMPLETED"));

        assertThat(result).isEqualTo(1);
    }

    @Test
    void COMPLETED에서_PENDING으로_역행전이하면_IllegalArgumentException() {
        when(adminReservationDao.selectReservationById(1)).thenReturn(existingReservationWithStatus("COMPLETED"));

        assertThatThrownBy(() -> adminReservationService.modifyReservationStatus(request(1, "PENDING")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void CANCELED된_예약은_추가전이할수없다() {
        when(adminReservationDao.selectReservationById(1)).thenReturn(existingReservationWithStatus("CANCELED"));

        assertThatThrownBy(() -> adminReservationService.modifyReservationStatus(request(1, "CONFIRMED")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void PENDING에서_COMPLETED로_건너뛰는_전이는_IllegalArgumentException() {
        when(adminReservationDao.selectReservationById(1)).thenReturn(existingReservationWithStatus("PENDING"));

        assertThatThrownBy(() -> adminReservationService.modifyReservationStatus(request(1, "COMPLETED")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 존재하지않는_예약이면_ResourceNotFoundException() {
        when(adminReservationDao.selectReservationById(999)).thenReturn(null);

        assertThatThrownBy(() -> adminReservationService.modifyReservationStatus(request(999, "CONFIRMED")))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
