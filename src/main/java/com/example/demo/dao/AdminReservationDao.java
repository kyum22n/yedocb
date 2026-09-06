package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.entity.Reservation;

/**
 * 파일명: AdminReservationDao.java
 * 설명: 관리자용 예약 관련 DAO 인터페이스
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 인터페이스 생성
 */

@Mapper
public interface AdminReservationDao {

    // 예약 등록
    public int insertReservation(Reservation reservation);

    // 예약 목록 조회
    public List<Reservation> selectAllReservations();

    // 회원별 예약 목록 조회
    public List<Reservation> selectReservationsByUId(String uId);

    // 담당자별 예약 목록 조회
    public List<Reservation> selectReservationsByAdminId(Integer adminId);

    // 예약 상태별 목록 조회
    public List<Reservation> selectReservationsByStatus(String reservationStatus);

    // PMS 연동 상태별 목록 조회
    public List<Reservation> selectReservationsByPmsSyncStatus(String pmsSyncStatus);

    // 예약 상세 조회
    public Reservation selectReservationById(Integer reservationId);

    // 예약 정보 수정
    public int updateReservation(Reservation reservation);

    // 예약 상태 수정
    public int updateReservationStatus(Reservation reservation);

    // PMS 연동 상태 수정
    public int updatePmsSyncStatus(Reservation reservation);

    // 예약 삭제
    public int deleteReservation(Integer reservationId);
}
