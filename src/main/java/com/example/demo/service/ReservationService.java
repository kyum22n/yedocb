package com.example.demo.service;

import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.ResourceNotFoundException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.ReservationDao;
import com.example.demo.dto.request.reservation.ReservationCancelRequestDto;
import com.example.demo.dto.request.reservation.ReservationCreateRequestDto;
import com.example.demo.dto.request.reservation.ReservationUpdateRequestDto;
import com.example.demo.dto.response.reservation.ReservationResponseDto;
import com.example.demo.entity.Reservation;

/**
 * 파일명: ReservationService.java
 * 설명: 사용자용 예약 관련 서비스 클래스
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 클래스 생성
 */

@Service
public class ReservationService {

    @Autowired
    private ReservationDao reservationDao;

    // 예약 등록 (생성된 예약의 PK를 반환 - insert row count 아님)
    public Integer createReservation(ReservationCreateRequestDto request) {

        if(request == null) {
            throw new IllegalArgumentException("예약 등록 요청 정보가 없습니다.");
        }

        if(request.getUId() == null) {
            throw new IllegalArgumentException("회원 ID는 필수입니다.");
        }

        if(request.getTreatmentId() == null) {
            throw new IllegalArgumentException("진료 항목 ID는 필수입니다.");
        }

        if(request.getReservationDate() == null) {
            throw new IllegalArgumentException("예약 일자는 필수입니다.");
        }

        if(request.getReservationTime() == null) {
            throw new IllegalArgumentException("예약 시간은 필수입니다.");
        }

        if(reservationDao.existsConflictingReservation(request.getReservationDate(), request.getReservationTime(), null)) {
            throw new DuplicateResourceException("이미 예약이 있는 시간입니다.");
        }

        Reservation reservation = new Reservation();
        reservation.setUId(request.getUId());
        reservation.setTreatmentId(request.getTreatmentId());
        reservation.setReservationDate(request.getReservationDate());
        reservation.setReservationTime(request.getReservationTime());
        reservation.setMemberMemo(request.getMemberMemo());

        reservationDao.insertReservation(reservation);
        return reservation.getReservationId();
    }

    // 예약 마감 시간대 조회 (특정 날짜에 이미 예약이 차있는 시간 목록 - 예약 폼에서 비활성화용)
    public List<LocalTime> getDisabledTimes(LocalDate reservationDate) {

        if(reservationDate == null) {
            throw new IllegalArgumentException("예약 일자는 필수입니다.");
        }

        return reservationDao.selectReservedTimesByDate(reservationDate);
    }

    // 회원별 예약 목록 조회
    public List<ReservationResponseDto> getReservationsByUId(String uId) {

        if(uId == null) {
            throw new IllegalArgumentException("회원 ID는 필수입니다.");
        }

        List<Reservation> reservations = reservationDao.selectReservationsByUId(uId);
        List<ReservationResponseDto> listResponse = new ArrayList<>();

        for(Reservation reservation : reservations) {
            ReservationResponseDto response = new ReservationResponseDto();
            response.setReservationId(reservation.getReservationId());
            response.setUId(reservation.getUId());
            response.setTreatmentId(reservation.getTreatmentId());
            response.setReservationDate(reservation.getReservationDate());
            response.setReservationTime(reservation.getReservationTime());
            response.setReservationStatus(reservation.getReservationStatus());
            response.setMemberMemo(reservation.getMemberMemo());
            response.setCreatedAt(reservation.getCreatedAt());
            response.setUpdatedAt(reservation.getUpdatedAt());

            listResponse.add(response);
        }

        return listResponse;
    }

    // 예약 상세 조회
    public ReservationResponseDto getReservationById(Integer reservationId, String uId) {

        if(reservationId == null) {
            throw new IllegalArgumentException("예약 ID는 필수입니다.");
        }

        if(uId == null) {
            throw new IllegalArgumentException("회원 ID는 필수입니다.");
        }

        Reservation reservation = reservationDao.selectReservationById(reservationId, uId);

        if(reservation == null) {
            throw new ResourceNotFoundException("존재하지 않는 예약입니다.");
        }

        ReservationResponseDto response = new ReservationResponseDto();
        response.setReservationId(reservation.getReservationId());
        response.setUId(reservation.getUId());
        response.setTreatmentId(reservation.getTreatmentId());
        response.setReservationDate(reservation.getReservationDate());
        response.setReservationTime(reservation.getReservationTime());
        response.setReservationStatus(reservation.getReservationStatus());
        response.setMemberMemo(reservation.getMemberMemo());
        response.setCreatedAt(reservation.getCreatedAt());
        response.setUpdatedAt(reservation.getUpdatedAt());

        return response;
    }

    // 예약 정보 수정
    public int modifyReservation(ReservationUpdateRequestDto request) {

        if(request == null) {
            throw new IllegalArgumentException("예약 수정 요청 정보가 없습니다.");
        }

        if(request.getReservationId() == null) {
            throw new IllegalArgumentException("예약 ID는 필수입니다.");
        }

        if(request.getUId() == null) {
            throw new IllegalArgumentException("회원 ID는 필수입니다.");
        }

        if(request.getTreatmentId() == null) {
            throw new IllegalArgumentException("진료 항목 ID는 필수입니다.");
        }

        if(request.getReservationDate() == null) {
            throw new IllegalArgumentException("예약 일자는 필수입니다.");
        }

        if(request.getReservationTime() == null) {
            throw new IllegalArgumentException("예약 시간은 필수입니다.");
        }

        Reservation existingReservation = reservationDao.selectReservationById(request.getReservationId(), request.getUId());

        if(existingReservation == null) {
            throw new ResourceNotFoundException("존재하지 않는 예약입니다.");
        }

        if(reservationDao.existsConflictingReservation(request.getReservationDate(), request.getReservationTime(), request.getReservationId())) {
            throw new DuplicateResourceException("이미 예약이 있는 시간입니다.");
        }

        Reservation reservation = new Reservation();
        reservation.setReservationId(request.getReservationId());
        reservation.setUId(request.getUId());
        reservation.setTreatmentId(request.getTreatmentId());
        reservation.setReservationDate(request.getReservationDate());
        reservation.setReservationTime(request.getReservationTime());
        reservation.setMemberMemo(request.getMemberMemo());

        return reservationDao.updateReservation(reservation);
    }

    // 예약 취소
    public int cancelReservation(ReservationCancelRequestDto request) {

        if(request == null) {
            throw new IllegalArgumentException("예약 취소 요청 정보가 없습니다.");
        }

        if(request.getReservationId() == null) {
            throw new IllegalArgumentException("예약 ID는 필수입니다.");
        }

        if(request.getUId() == null) {
            throw new IllegalArgumentException("회원 ID는 필수입니다.");
        }

        Reservation existingReservation = reservationDao.selectReservationById(request.getReservationId(), request.getUId());

        if(existingReservation == null) {
            throw new ResourceNotFoundException("존재하지 않는 예약입니다.");
        }

        return reservationDao.cancelReservation(request.getReservationId(), request.getUId());
    }
}
