package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.AdminReservationDao;
import com.example.demo.dto.request.reservation.AdminPmsSyncStatusUpdateRequestDto;
import com.example.demo.dto.request.reservation.AdminReservationCreateRequestDto;
import com.example.demo.dto.request.reservation.AdminReservationStatusUpdateRequestDto;
import com.example.demo.dto.request.reservation.AdminReservationUpdateRequestDto;
import com.example.demo.dto.response.reservation.AdminReservationResponseDto;
import com.example.demo.entity.Reservation;

/**
 * 파일명: AdminReservationService.java
 * 설명: 관리자용 예약 관련 서비스 클래스
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 클래스 생성
 */

@Service
public class AdminReservationService {

    @Autowired
    private AdminReservationDao adminReservationDao;

    // 예약 등록 (생성된 예약의 PK를 반환 - insert row count 아님. 상담->예약 전환 시
    // 이 ID를 AdminConsultationConvertRequestDto.reservationId에 사용한다)
    public Integer createReservation(AdminReservationCreateRequestDto request) {

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

        if(request.getReservationStatus() != null && !request.getReservationStatus().isBlank()
                && !request.getReservationStatus().equals("PENDING")
                && !request.getReservationStatus().equals("CONFIRMED")
                && !request.getReservationStatus().equals("COMPLETED")
                && !request.getReservationStatus().equals("CANCELED")
                && !request.getReservationStatus().equals("NO_SHOW")) {
            throw new IllegalArgumentException("올바르지 않은 예약 상태입니다.");
        }

        if(request.getPmsSyncStatus() != null && !request.getPmsSyncStatus().isBlank()
                && !request.getPmsSyncStatus().equals("PENDING")
                && !request.getPmsSyncStatus().equals("SUCCESS")
                && !request.getPmsSyncStatus().equals("FAILED")) {
            throw new IllegalArgumentException("올바르지 않은 PMS 연동 상태입니다.");
        }

        Reservation reservation = new Reservation();
        reservation.setUId(request.getUId());
        reservation.setTreatmentId(request.getTreatmentId());
        reservation.setAdminId(request.getAdminId());
        reservation.setReservationDate(request.getReservationDate());
        reservation.setReservationTime(request.getReservationTime());
        reservation.setReservationStatus(request.getReservationStatus());
        reservation.setMemberMemo(request.getMemberMemo());
        reservation.setAdminMemo(request.getAdminMemo());
        reservation.setPmsSyncStatus(request.getPmsSyncStatus());

        adminReservationDao.insertReservation(reservation);
        return reservation.getReservationId();
    }

    // 예약 목록 조회
    public List<AdminReservationResponseDto> getAllReservations() {

        List<Reservation> reservations = adminReservationDao.selectAllReservations();
        List<AdminReservationResponseDto> listResponse = new ArrayList<>();

        for(Reservation reservation : reservations) {
            AdminReservationResponseDto response = new AdminReservationResponseDto();
            response.setReservationId(reservation.getReservationId());
            response.setUId(reservation.getUId());
            response.setTreatmentId(reservation.getTreatmentId());
            response.setAdminId(reservation.getAdminId());
            response.setReservationDate(reservation.getReservationDate());
            response.setReservationTime(reservation.getReservationTime());
            response.setReservationStatus(reservation.getReservationStatus());
            response.setMemberMemo(reservation.getMemberMemo());
            response.setAdminMemo(reservation.getAdminMemo());
            response.setPmsSyncStatus(reservation.getPmsSyncStatus());
            response.setCreatedAt(reservation.getCreatedAt());
            response.setUpdatedAt(reservation.getUpdatedAt());

            listResponse.add(response);
        }

        return listResponse;
    }

    // 회원별 예약 목록 조회
    public List<AdminReservationResponseDto> getReservationsByUId(String uId) {

        if(uId == null) {
            throw new IllegalArgumentException("회원 ID는 필수입니다.");
        }

        List<Reservation> reservations = adminReservationDao.selectReservationsByUId(uId);
        List<AdminReservationResponseDto> listResponse = new ArrayList<>();

        for(Reservation reservation : reservations) {
            AdminReservationResponseDto response = new AdminReservationResponseDto();
            response.setReservationId(reservation.getReservationId());
            response.setUId(reservation.getUId());
            response.setTreatmentId(reservation.getTreatmentId());
            response.setAdminId(reservation.getAdminId());
            response.setReservationDate(reservation.getReservationDate());
            response.setReservationTime(reservation.getReservationTime());
            response.setReservationStatus(reservation.getReservationStatus());
            response.setMemberMemo(reservation.getMemberMemo());
            response.setAdminMemo(reservation.getAdminMemo());
            response.setPmsSyncStatus(reservation.getPmsSyncStatus());
            response.setCreatedAt(reservation.getCreatedAt());
            response.setUpdatedAt(reservation.getUpdatedAt());

            listResponse.add(response);
        }

        return listResponse;
    }

    // 담당자별 예약 목록 조회
    public List<AdminReservationResponseDto> getReservationsByAdminId(Integer adminId) {

        if(adminId == null) {
            throw new IllegalArgumentException("담당자 ID는 필수입니다.");
        }

        List<Reservation> reservations = adminReservationDao.selectReservationsByAdminId(adminId);
        List<AdminReservationResponseDto> listResponse = new ArrayList<>();

        for(Reservation reservation : reservations) {
            AdminReservationResponseDto response = new AdminReservationResponseDto();
            response.setReservationId(reservation.getReservationId());
            response.setUId(reservation.getUId());
            response.setTreatmentId(reservation.getTreatmentId());
            response.setAdminId(reservation.getAdminId());
            response.setReservationDate(reservation.getReservationDate());
            response.setReservationTime(reservation.getReservationTime());
            response.setReservationStatus(reservation.getReservationStatus());
            response.setMemberMemo(reservation.getMemberMemo());
            response.setAdminMemo(reservation.getAdminMemo());
            response.setPmsSyncStatus(reservation.getPmsSyncStatus());
            response.setCreatedAt(reservation.getCreatedAt());
            response.setUpdatedAt(reservation.getUpdatedAt());

            listResponse.add(response);
        }

        return listResponse;
    }

    // 예약 상태별 목록 조회
    public List<AdminReservationResponseDto> getReservationsByStatus(String reservationStatus) {

        if(reservationStatus == null || reservationStatus.isBlank()) {
            throw new IllegalArgumentException("예약 상태는 필수입니다.");
        }

        if(!reservationStatus.equals("PENDING")
                && !reservationStatus.equals("CONFIRMED")
                && !reservationStatus.equals("COMPLETED")
                && !reservationStatus.equals("CANCELED")
                && !reservationStatus.equals("NO_SHOW")) {
            throw new IllegalArgumentException("올바르지 않은 예약 상태입니다.");
        }

        List<Reservation> reservations = adminReservationDao.selectReservationsByStatus(reservationStatus);
        List<AdminReservationResponseDto> listResponse = new ArrayList<>();

        for(Reservation reservation : reservations) {
            AdminReservationResponseDto response = new AdminReservationResponseDto();
            response.setReservationId(reservation.getReservationId());
            response.setUId(reservation.getUId());
            response.setTreatmentId(reservation.getTreatmentId());
            response.setAdminId(reservation.getAdminId());
            response.setReservationDate(reservation.getReservationDate());
            response.setReservationTime(reservation.getReservationTime());
            response.setReservationStatus(reservation.getReservationStatus());
            response.setMemberMemo(reservation.getMemberMemo());
            response.setAdminMemo(reservation.getAdminMemo());
            response.setPmsSyncStatus(reservation.getPmsSyncStatus());
            response.setCreatedAt(reservation.getCreatedAt());
            response.setUpdatedAt(reservation.getUpdatedAt());

            listResponse.add(response);
        }

        return listResponse;
    }

    // PMS 연동 상태별 목록 조회
    public List<AdminReservationResponseDto> getReservationsByPmsSyncStatus(String pmsSyncStatus) {

        if(pmsSyncStatus == null || pmsSyncStatus.isBlank()) {
            throw new IllegalArgumentException("PMS 연동 상태는 필수입니다.");
        }

        if(!pmsSyncStatus.equals("PENDING")
                && !pmsSyncStatus.equals("SUCCESS")
                && !pmsSyncStatus.equals("FAILED")) {
            throw new IllegalArgumentException("올바르지 않은 PMS 연동 상태입니다.");
        }

        List<Reservation> reservations = adminReservationDao.selectReservationsByPmsSyncStatus(pmsSyncStatus);
        List<AdminReservationResponseDto> listResponse = new ArrayList<>();

        for(Reservation reservation : reservations) {
            AdminReservationResponseDto response = new AdminReservationResponseDto();
            response.setReservationId(reservation.getReservationId());
            response.setUId(reservation.getUId());
            response.setTreatmentId(reservation.getTreatmentId());
            response.setAdminId(reservation.getAdminId());
            response.setReservationDate(reservation.getReservationDate());
            response.setReservationTime(reservation.getReservationTime());
            response.setReservationStatus(reservation.getReservationStatus());
            response.setMemberMemo(reservation.getMemberMemo());
            response.setAdminMemo(reservation.getAdminMemo());
            response.setPmsSyncStatus(reservation.getPmsSyncStatus());
            response.setCreatedAt(reservation.getCreatedAt());
            response.setUpdatedAt(reservation.getUpdatedAt());

            listResponse.add(response);
        }

        return listResponse;
    }

    // 예약 상세 조회
    public AdminReservationResponseDto getReservationById(Integer reservationId) {

        if(reservationId == null) {
            throw new IllegalArgumentException("예약 ID는 필수입니다.");
        }

        Reservation reservation = adminReservationDao.selectReservationById(reservationId);

        if(reservation == null) {
            throw new ResourceNotFoundException("존재하지 않는 예약입니다.");
        }

        AdminReservationResponseDto response = new AdminReservationResponseDto();
        response.setReservationId(reservation.getReservationId());
        response.setUId(reservation.getUId());
        response.setTreatmentId(reservation.getTreatmentId());
        response.setAdminId(reservation.getAdminId());
        response.setReservationDate(reservation.getReservationDate());
        response.setReservationTime(reservation.getReservationTime());
        response.setReservationStatus(reservation.getReservationStatus());
        response.setMemberMemo(reservation.getMemberMemo());
        response.setAdminMemo(reservation.getAdminMemo());
        response.setPmsSyncStatus(reservation.getPmsSyncStatus());
        response.setCreatedAt(reservation.getCreatedAt());
        response.setUpdatedAt(reservation.getUpdatedAt());

        return response;
    }

    // 예약 정보 수정
    public int modifyReservation(AdminReservationUpdateRequestDto request) {

        if(request == null) {
            throw new IllegalArgumentException("예약 수정 요청 정보가 없습니다.");
        }

        if(request.getReservationId() == null) {
            throw new IllegalArgumentException("예약 ID는 필수입니다.");
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

        if(request.getReservationStatus() == null || request.getReservationStatus().isBlank()) {
            throw new IllegalArgumentException("예약 상태는 필수입니다.");
        }

        if(!request.getReservationStatus().equals("PENDING")
                && !request.getReservationStatus().equals("CONFIRMED")
                && !request.getReservationStatus().equals("COMPLETED")
                && !request.getReservationStatus().equals("CANCELED")
                && !request.getReservationStatus().equals("NO_SHOW")) {
            throw new IllegalArgumentException("올바르지 않은 예약 상태입니다.");
        }

        Reservation existingReservation = adminReservationDao.selectReservationById(request.getReservationId());

        if(existingReservation == null) {
            throw new ResourceNotFoundException("존재하지 않는 예약입니다.");
        }

        Reservation reservation = new Reservation();
        reservation.setReservationId(request.getReservationId());
        reservation.setTreatmentId(request.getTreatmentId());
        reservation.setAdminId(request.getAdminId());
        reservation.setReservationDate(request.getReservationDate());
        reservation.setReservationTime(request.getReservationTime());
        reservation.setReservationStatus(request.getReservationStatus());
        reservation.setMemberMemo(request.getMemberMemo());
        reservation.setAdminMemo(request.getAdminMemo());

        return adminReservationDao.updateReservation(reservation);
    }

    // 예약 상태 수정
    public int modifyReservationStatus(AdminReservationStatusUpdateRequestDto request) {

        if(request == null) {
            throw new IllegalArgumentException("예약 상태 수정 요청 정보가 없습니다.");
        }

        if(request.getReservationId() == null) {
            throw new IllegalArgumentException("예약 ID는 필수입니다.");
        }

        if(request.getReservationStatus() == null || request.getReservationStatus().isBlank()) {
            throw new IllegalArgumentException("예약 상태는 필수입니다.");
        }

        if(!request.getReservationStatus().equals("PENDING")
                && !request.getReservationStatus().equals("CONFIRMED")
                && !request.getReservationStatus().equals("COMPLETED")
                && !request.getReservationStatus().equals("CANCELED")
                && !request.getReservationStatus().equals("NO_SHOW")) {
            throw new IllegalArgumentException("올바르지 않은 예약 상태입니다.");
        }

        Reservation existingReservation = adminReservationDao.selectReservationById(request.getReservationId());

        if(existingReservation == null) {
            throw new ResourceNotFoundException("존재하지 않는 예약입니다.");
        }

        Reservation reservation = new Reservation();
        reservation.setReservationId(request.getReservationId());
        reservation.setAdminId(request.getAdminId());
        reservation.setReservationStatus(request.getReservationStatus());
        reservation.setAdminMemo(request.getAdminMemo());

        return adminReservationDao.updateReservationStatus(reservation);
    }

    // PMS 연동 상태 수정
    public int modifyPmsSyncStatus(AdminPmsSyncStatusUpdateRequestDto request) {

        if(request == null) {
            throw new IllegalArgumentException("PMS 연동 상태 수정 요청 정보가 없습니다.");
        }

        if(request.getReservationId() == null) {
            throw new IllegalArgumentException("예약 ID는 필수입니다.");
        }

        if(request.getPmsSyncStatus() == null || request.getPmsSyncStatus().isBlank()) {
            throw new IllegalArgumentException("PMS 연동 상태는 필수입니다.");
        }

        if(!request.getPmsSyncStatus().equals("PENDING")
                && !request.getPmsSyncStatus().equals("SUCCESS")
                && !request.getPmsSyncStatus().equals("FAILED")) {
            throw new IllegalArgumentException("올바르지 않은 PMS 연동 상태입니다.");
        }

        Reservation existingReservation = adminReservationDao.selectReservationById(request.getReservationId());

        if(existingReservation == null) {
            throw new ResourceNotFoundException("존재하지 않는 예약입니다.");
        }

        Reservation reservation = new Reservation();
        reservation.setReservationId(request.getReservationId());
        reservation.setPmsSyncStatus(request.getPmsSyncStatus());

        return adminReservationDao.updatePmsSyncStatus(reservation);
    }

    // 예약 삭제
    public int removeReservation(Integer reservationId) {

        if(reservationId == null) {
            throw new IllegalArgumentException("예약 ID는 필수입니다.");
        }

        Reservation existingReservation = adminReservationDao.selectReservationById(reservationId);

        if(existingReservation == null) {
            throw new ResourceNotFoundException("존재하지 않는 예약입니다.");
        }

        return adminReservationDao.deleteReservation(reservationId);
    }
}
