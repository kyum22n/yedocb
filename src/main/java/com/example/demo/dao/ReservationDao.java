package com.example.demo.dao;

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
 */

@Mapper
public interface ReservationDao {

    // 예약 등록
    public int insertReservation(Reservation reservation);

    // 회원별 예약 목록 조회
    public List<Reservation> selectReservationsByMemberId(Integer memberId);

    // 예약 상세 조회
    public Reservation selectReservationById(@Param("reservationId") Integer reservationId,
                                             @Param("memberId") Integer memberId);

    // 예약 수정
    public int updateReservation(Reservation reservation);

    // 예약 취소
    public int cancelReservation(@Param("reservationId") Integer reservationId,
                                 @Param("memberId") Integer memberId);
}
