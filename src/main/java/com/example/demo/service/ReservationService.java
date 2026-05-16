package com.example.demo.service;

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

    // 예약 등록
    public int createReservation(ReservationCreateRequestDto request) {

        if(request == null) {
            throw new IllegalArgumentException("예약 등록 요청 정보가 없습니다.");
        }

        if(request.getMemberId() == null) {
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

        Reservation reservation = new Reservation();
        reservation.setMemberId(request.getMemberId());
        reservation.setTreatmentId(request.getTreatmentId());
        reservation.setReservationDate(request.getReservationDate());
        reservation.setReservationTime(request.getReservationTime());
        reservation.setMemberMemo(request.getMemberMemo());

        return reservationDao.insertReservation(reservation);
    }

    // 회원별 예약 목록 조회
    public List<ReservationResponseDto> getReservationsByMemberId(Integer memberId) {

        if(memberId == null) {
            throw new IllegalArgumentException("회원 ID는 필수입니다.");
        }

        List<Reservation> reservations = reservationDao.selectReservationsByMemberId(memberId);
        List<ReservationResponseDto> listResponse = new ArrayList<>();

        for(Reservation reservation : reservations) {
            ReservationResponseDto response = new ReservationResponseDto();
            response.setReservationId(reservation.getReservationId());
            response.setMemberId(reservation.getMemberId());
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
    public ReservationResponseDto getReservationById(Integer reservationId, Integer memberId) {

        if(reservationId == null) {
            throw new IllegalArgumentException("예약 ID는 필수입니다.");
        }

        if(memberId == null) {
            throw new IllegalArgumentException("회원 ID는 필수입니다.");
        }

        Reservation reservation = reservationDao.selectReservationById(reservationId, memberId);

        if(reservation == null) {
            throw new IllegalArgumentException("존재하지 않는 예약입니다.");
        }

        ReservationResponseDto response = new ReservationResponseDto();
        response.setReservationId(reservation.getReservationId());
        response.setMemberId(reservation.getMemberId());
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

        if(request.getMemberId() == null) {
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

        Reservation existingReservation = reservationDao.selectReservationById(request.getReservationId(), request.getMemberId());

        if(existingReservation == null) {
            throw new IllegalArgumentException("존재하지 않는 예약입니다.");
        }

        Reservation reservation = new Reservation();
        reservation.setReservationId(request.getReservationId());
        reservation.setMemberId(request.getMemberId());
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

        if(request.getMemberId() == null) {
            throw new IllegalArgumentException("회원 ID는 필수입니다.");
        }

        Reservation existingReservation = reservationDao.selectReservationById(request.getReservationId(), request.getMemberId());

        if(existingReservation == null) {
            throw new IllegalArgumentException("존재하지 않는 예약입니다.");
        }

        return reservationDao.cancelReservation(request.getReservationId(), request.getMemberId());
    }
}
