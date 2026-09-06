package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.dao.AdminStatisticsDao;
import com.example.demo.dto.request.statistics.StatisticsSearchRequestDto;
import com.example.demo.dto.response.statistics.ReservationStatisticsResponseDto;

/**
 * 파일명: AdminStatisticsServiceTest.java
 * 설명: AdminStatisticsService 단위 테스트 (Mockito, 실제 DB 미사용)
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | Phase 2 단위 테스트 작성
 */
@ExtendWith(MockitoExtension.class)
class AdminStatisticsServiceTest {

    @Mock
    private AdminStatisticsDao statisticsDao;

    @InjectMocks
    private AdminStatisticsService statisticsService;

    @Test
    void 예약통계조회_성공시_노쇼율을_계산한다() {
        StatisticsSearchRequestDto request = new StatisticsSearchRequestDto();
        request.setStartDate(LocalDate.of(2026, 1, 1));
        request.setEndDate(LocalDate.of(2026, 1, 31));

        ReservationStatisticsResponseDto daoResponse = new ReservationStatisticsResponseDto();
        daoResponse.setTotalReservationCount(100);
        daoResponse.setPendingCount(10);
        daoResponse.setConfirmedCount(50);
        daoResponse.setCompletedCount(30);
        daoResponse.setCanceledCount(5);
        daoResponse.setNoShowCount(5);

        when(statisticsDao.selectReservationStatistics(request)).thenReturn(daoResponse);

        ReservationStatisticsResponseDto result = statisticsService.getReservationStatistics(request);

        assertThat(result.getTotalReservationCount()).isEqualTo(100);
        assertThat(result.getNoShowRate()).isEqualTo(5.0);
    }

    @Test
    void 예약통계조회_결과가없으면_0으로_채운다() {
        StatisticsSearchRequestDto request = new StatisticsSearchRequestDto();

        when(statisticsDao.selectReservationStatistics(request)).thenReturn(null);

        ReservationStatisticsResponseDto result = statisticsService.getReservationStatistics(request);

        assertThat(result.getTotalReservationCount()).isEqualTo(0);
        assertThat(result.getNoShowRate()).isEqualTo(0.0);
    }

    @Test
    void 통계조회_종료일이시작일보다빠르면_IllegalArgumentException() {
        StatisticsSearchRequestDto request = new StatisticsSearchRequestDto();
        request.setStartDate(LocalDate.of(2026, 2, 1));
        request.setEndDate(LocalDate.of(2026, 1, 1));

        assertThatThrownBy(() -> statisticsService.getReservationStatistics(request))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
