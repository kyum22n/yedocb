package com.example.demo.dao;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.entity.Reservation;

/**
 * 파일명: ReservationDao.java
 * 설명: 사용자용 예약 관련 DAO 인터페이스
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 인터페이스 생성
 * 2026-09-06 | 리팩토링 | 예약 마감 시간대 조회 추가 (프론트엔드 세션 요청 대응)
 */

@Mapper
public interface ReservationDao {

    // 예약 등록
    public int insertReservation(Reservation reservation);

    // 특정 날짜에 이미 예약이 차있는 시간 목록 조회 (CANCELED/NO_SHOW 제외)
    public List<LocalTime> selectReservedTimesByDate(@Param("reservationDate") LocalDate reservationDate);

    // 동일 날짜/시간에 이미 유효한 예약이 있는지 확인 (수정 시 자기 자신은 제외)
    public boolean existsConflictingReservation(@Param("reservationDate") LocalDate reservationDate,
                                                 @Param("reservationTime") LocalTime reservationTime,
                                                 @Param("excludeReservationId") Integer excludeReservationId);

    // 회원별 예약 목록 조회
    public List<Reservation> selectReservationsByUId(String uId);

    // 예약 상세 조회
    public Reservation selectReservationById(@Param("reservationId") Integer reservationId,
                                             @Param("uId") String uId);

    // 예약 수정
    public int updateReservation(Reservation reservation);

    // 예약 취소
    public int cancelReservation(@Param("reservationId") Integer reservationId,
                                 @Param("uId") String uId);
}
