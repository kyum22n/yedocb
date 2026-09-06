package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.AdminConsultationDao;
import com.example.demo.dto.request.consultation.AdminConsultationConvertRequestDto;
import com.example.demo.dto.request.consultation.AdminConsultationCreateRequestDto;
import com.example.demo.dto.request.consultation.AdminConsultationStatusUpdateRequestDto;
import com.example.demo.dto.request.consultation.AdminConsultationUpdateRequestDto;
import com.example.demo.dto.response.consultation.AdminConsultationResponseDto;
import com.example.demo.entity.Consultation;

/**
 * 파일명: AdminConsultationService.java
 * 설명: 관리자용 상담 관련 서비스 클래스
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 클래스 생성
 */

@Service
public class AdminConsultationService {

    @Autowired
    private AdminConsultationDao adminConsultationDao;

    // 상담 등록
    public int createConsultation(AdminConsultationCreateRequestDto request) {

        if(request == null) {
            throw new IllegalArgumentException("상담 등록 요청 정보가 없습니다.");
        }

        if(request.getUId() == null) {
            throw new IllegalArgumentException("회원 ID는 필수입니다.");
        }

        if(request.getConsultationStatus() != null && !request.getConsultationStatus().isBlank()
                && !request.getConsultationStatus().equals("RECEIVED")
                && !request.getConsultationStatus().equals("SCHEDULED")
                && !request.getConsultationStatus().equals("COMPLETED")
                && !request.getConsultationStatus().equals("CONVERTED")
                && !request.getConsultationStatus().equals("CANCELED")) {
            throw new IllegalArgumentException("올바르지 않은 상담 상태입니다.");
        }

        Consultation consultation = new Consultation();
        consultation.setUId(request.getUId());
        consultation.setReservationId(request.getReservationId());
        consultation.setTreatmentId(request.getTreatmentId());
        consultation.setAdminId(request.getAdminId());
        consultation.setConsultationStatus(request.getConsultationStatus());
        consultation.setConsultationMemo(request.getConsultationMemo());
        consultation.setPreferredDate(request.getPreferredDate());
        consultation.setPreferredTime(request.getPreferredTime());

        return adminConsultationDao.insertConsultation(consultation);
    }

    // 상담 목록 조회
    public List<AdminConsultationResponseDto> getAllConsultations() {

        List<Consultation> consultations = adminConsultationDao.selectAllConsultations();
        List<AdminConsultationResponseDto> listResponse = new ArrayList<>();

        for(Consultation consultation : consultations) {
            AdminConsultationResponseDto response = new AdminConsultationResponseDto();
            response.setConsultationId(consultation.getConsultationId());
            response.setUId(consultation.getUId());
            response.setReservationId(consultation.getReservationId());
            response.setTreatmentId(consultation.getTreatmentId());
            response.setAdminId(consultation.getAdminId());
            response.setConsultationStatus(consultation.getConsultationStatus());
            response.setConsultationMemo(consultation.getConsultationMemo());
            response.setPreferredDate(consultation.getPreferredDate());
            response.setPreferredTime(consultation.getPreferredTime());
            response.setCreatedAt(consultation.getCreatedAt());
            response.setUpdatedAt(consultation.getUpdatedAt());

            listResponse.add(response);
        }

        return listResponse;
    }

    // 회원별 상담 목록 조회
    public List<AdminConsultationResponseDto> getConsultationsByUId(String uId) {

        if(uId == null) {
            throw new IllegalArgumentException("회원 ID는 필수입니다.");
        }

        List<Consultation> consultations = adminConsultationDao.selectConsultationsByUId(uId);
        List<AdminConsultationResponseDto> listResponse = new ArrayList<>();

        for(Consultation consultation : consultations) {
            AdminConsultationResponseDto response = new AdminConsultationResponseDto();
            response.setConsultationId(consultation.getConsultationId());
            response.setUId(consultation.getUId());
            response.setReservationId(consultation.getReservationId());
            response.setTreatmentId(consultation.getTreatmentId());
            response.setAdminId(consultation.getAdminId());
            response.setConsultationStatus(consultation.getConsultationStatus());
            response.setConsultationMemo(consultation.getConsultationMemo());
            response.setPreferredDate(consultation.getPreferredDate());
            response.setPreferredTime(consultation.getPreferredTime());
            response.setCreatedAt(consultation.getCreatedAt());
            response.setUpdatedAt(consultation.getUpdatedAt());

            listResponse.add(response);
        }

        return listResponse;
    }

    // 담당자별 상담 목록 조회
    public List<AdminConsultationResponseDto> getConsultationsByAdminId(Integer adminId) {

        if(adminId == null) {
            throw new IllegalArgumentException("담당자 ID는 필수입니다.");
        }

        List<Consultation> consultations = adminConsultationDao.selectConsultationsByAdminId(adminId);
        List<AdminConsultationResponseDto> listResponse = new ArrayList<>();

        for(Consultation consultation : consultations) {
            AdminConsultationResponseDto response = new AdminConsultationResponseDto();
            response.setConsultationId(consultation.getConsultationId());
            response.setUId(consultation.getUId());
            response.setReservationId(consultation.getReservationId());
            response.setTreatmentId(consultation.getTreatmentId());
            response.setAdminId(consultation.getAdminId());
            response.setConsultationStatus(consultation.getConsultationStatus());
            response.setConsultationMemo(consultation.getConsultationMemo());
            response.setPreferredDate(consultation.getPreferredDate());
            response.setPreferredTime(consultation.getPreferredTime());
            response.setCreatedAt(consultation.getCreatedAt());
            response.setUpdatedAt(consultation.getUpdatedAt());

            listResponse.add(response);
        }

        return listResponse;
    }

    // 상담 상태별 목록 조회
    public List<AdminConsultationResponseDto> getConsultationsByStatus(String consultationStatus) {

        if(consultationStatus == null || consultationStatus.isBlank()) {
            throw new IllegalArgumentException("상담 상태는 필수입니다.");
        }

        if(!consultationStatus.equals("RECEIVED")
                && !consultationStatus.equals("SCHEDULED")
                && !consultationStatus.equals("COMPLETED")
                && !consultationStatus.equals("CONVERTED")
                && !consultationStatus.equals("CANCELED")) {
            throw new IllegalArgumentException("올바르지 않은 상담 상태입니다.");
        }

        List<Consultation> consultations = adminConsultationDao.selectConsultationsByStatus(consultationStatus);
        List<AdminConsultationResponseDto> listResponse = new ArrayList<>();

        for(Consultation consultation : consultations) {
            AdminConsultationResponseDto response = new AdminConsultationResponseDto();
            response.setConsultationId(consultation.getConsultationId());
            response.setUId(consultation.getUId());
            response.setReservationId(consultation.getReservationId());
            response.setTreatmentId(consultation.getTreatmentId());
            response.setAdminId(consultation.getAdminId());
            response.setConsultationStatus(consultation.getConsultationStatus());
            response.setConsultationMemo(consultation.getConsultationMemo());
            response.setPreferredDate(consultation.getPreferredDate());
            response.setPreferredTime(consultation.getPreferredTime());
            response.setCreatedAt(consultation.getCreatedAt());
            response.setUpdatedAt(consultation.getUpdatedAt());

            listResponse.add(response);
        }

        return listResponse;
    }

    // 상담 상세 조회
    public AdminConsultationResponseDto getConsultationById(Integer consultationId) {

        if(consultationId == null) {
            throw new IllegalArgumentException("상담 ID는 필수입니다.");
        }

        Consultation consultation = adminConsultationDao.selectConsultationById(consultationId);

        if(consultation == null) {
            throw new IllegalArgumentException("존재하지 않는 상담입니다.");
        }

        AdminConsultationResponseDto response = new AdminConsultationResponseDto();
        response.setConsultationId(consultation.getConsultationId());
        response.setUId(consultation.getUId());
        response.setReservationId(consultation.getReservationId());
        response.setTreatmentId(consultation.getTreatmentId());
        response.setAdminId(consultation.getAdminId());
        response.setConsultationStatus(consultation.getConsultationStatus());
        response.setConsultationMemo(consultation.getConsultationMemo());
        response.setPreferredDate(consultation.getPreferredDate());
        response.setPreferredTime(consultation.getPreferredTime());
        response.setCreatedAt(consultation.getCreatedAt());
        response.setUpdatedAt(consultation.getUpdatedAt());

        return response;
    }

    // 상담 정보 수정
    public int modifyConsultation(AdminConsultationUpdateRequestDto request) {

        if(request == null) {
            throw new IllegalArgumentException("상담 수정 요청 정보가 없습니다.");
        }

        if(request.getConsultationId() == null) {
            throw new IllegalArgumentException("상담 ID는 필수입니다.");
        }

        if(request.getConsultationStatus() == null || request.getConsultationStatus().isBlank()) {
            throw new IllegalArgumentException("상담 상태는 필수입니다.");
        }

        if(!request.getConsultationStatus().equals("RECEIVED")
                && !request.getConsultationStatus().equals("SCHEDULED")
                && !request.getConsultationStatus().equals("COMPLETED")
                && !request.getConsultationStatus().equals("CONVERTED")
                && !request.getConsultationStatus().equals("CANCELED")) {
            throw new IllegalArgumentException("올바르지 않은 상담 상태입니다.");
        }

        Consultation existingConsultation = adminConsultationDao.selectConsultationById(request.getConsultationId());

        if(existingConsultation == null) {
            throw new IllegalArgumentException("존재하지 않는 상담입니다.");
        }

        Consultation consultation = new Consultation();
        consultation.setConsultationId(request.getConsultationId());
        consultation.setReservationId(request.getReservationId());
        consultation.setTreatmentId(request.getTreatmentId());
        consultation.setAdminId(request.getAdminId());
        consultation.setConsultationStatus(request.getConsultationStatus());
        consultation.setConsultationMemo(request.getConsultationMemo());
        consultation.setPreferredDate(request.getPreferredDate());
        consultation.setPreferredTime(request.getPreferredTime());

        return adminConsultationDao.updateConsultation(consultation);
    }

    // 상담 상태 수정
    public int modifyConsultationStatus(AdminConsultationStatusUpdateRequestDto request) {

        if(request == null) {
            throw new IllegalArgumentException("상담 상태 수정 요청 정보가 없습니다.");
        }

        if(request.getConsultationId() == null) {
            throw new IllegalArgumentException("상담 ID는 필수입니다.");
        }

        if(request.getConsultationStatus() == null || request.getConsultationStatus().isBlank()) {
            throw new IllegalArgumentException("상담 상태는 필수입니다.");
        }

        if(!request.getConsultationStatus().equals("RECEIVED")
                && !request.getConsultationStatus().equals("SCHEDULED")
                && !request.getConsultationStatus().equals("COMPLETED")
                && !request.getConsultationStatus().equals("CONVERTED")
                && !request.getConsultationStatus().equals("CANCELED")) {
            throw new IllegalArgumentException("올바르지 않은 상담 상태입니다.");
        }

        Consultation existingConsultation = adminConsultationDao.selectConsultationById(request.getConsultationId());

        if(existingConsultation == null) {
            throw new IllegalArgumentException("존재하지 않는 상담입니다.");
        }

        Consultation consultation = new Consultation();
        consultation.setConsultationId(request.getConsultationId());
        consultation.setAdminId(request.getAdminId());
        consultation.setConsultationStatus(request.getConsultationStatus());
        consultation.setConsultationMemo(request.getConsultationMemo());

        return adminConsultationDao.updateConsultationStatus(consultation);
    }

    // 상담 예약 전환
    public int convertConsultationToReservation(AdminConsultationConvertRequestDto request) {

        if(request == null) {
            throw new IllegalArgumentException("상담 예약 전환 요청 정보가 없습니다.");
        }

        if(request.getConsultationId() == null) {
            throw new IllegalArgumentException("상담 ID는 필수입니다.");
        }

        if(request.getReservationId() == null) {
            throw new IllegalArgumentException("예약 ID는 필수입니다.");
        }

        Consultation existingConsultation = adminConsultationDao.selectConsultationById(request.getConsultationId());

        if(existingConsultation == null) {
            throw new IllegalArgumentException("존재하지 않는 상담입니다.");
        }

        Consultation consultation = new Consultation();
        consultation.setConsultationId(request.getConsultationId());
        consultation.setReservationId(request.getReservationId());
        consultation.setAdminId(request.getAdminId());
        consultation.setConsultationMemo(request.getConsultationMemo());

        return adminConsultationDao.convertConsultationToReservation(consultation);
    }

    // 상담 삭제
    public int removeConsultation(Integer consultationId) {

        if(consultationId == null) {
            throw new IllegalArgumentException("상담 ID는 필수입니다.");
        }

        Consultation existingConsultation = adminConsultationDao.selectConsultationById(consultationId);

        if(existingConsultation == null) {
            throw new IllegalArgumentException("존재하지 않는 상담입니다.");
        }

        return adminConsultationDao.deleteConsultation(consultationId);
    }
}
