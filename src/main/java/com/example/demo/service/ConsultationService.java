package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.ConsultationDao;
import com.example.demo.dto.request.consultation.ConsultationCancelRequestDto;
import com.example.demo.dto.request.consultation.ConsultationCreateRequestDto;
import com.example.demo.dto.request.consultation.ConsultationUpdateRequestDto;
import com.example.demo.dto.response.consultation.ConsultationResponseDto;
import com.example.demo.entity.Consultation;

/**
 * 파일명: ConsultationService.java
 * 설명: 사용자용 상담 관련 서비스 클래스
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 클래스 생성
 */

@Service
public class ConsultationService {

    @Autowired
    private ConsultationDao consultationDao;

    // 상담 등록
    public int createConsultation(ConsultationCreateRequestDto request) {

        if(request == null) {
            throw new IllegalArgumentException("상담 등록 요청 정보가 없습니다.");
        }

        if(request.getMemberId() == null) {
            throw new IllegalArgumentException("회원 ID는 필수입니다.");
        }

        if(request.getTreatmentId() == null) {
            throw new IllegalArgumentException("진료 항목 ID는 필수입니다.");
        }

        Consultation consultation = new Consultation();
        consultation.setMemberId(request.getMemberId());
        consultation.setTreatmentId(request.getTreatmentId());
        consultation.setConsultationMemo(request.getConsultationMemo());
        consultation.setPreferredDate(request.getPreferredDate());
        consultation.setPreferredTime(request.getPreferredTime());

        return consultationDao.insertConsultation(consultation);
    }

    // 회원별 상담 목록 조회
    public List<ConsultationResponseDto> getConsultationsByMemberId(Integer memberId) {

        if(memberId == null) {
            throw new IllegalArgumentException("회원 ID는 필수입니다.");
        }

        List<Consultation> consultations = consultationDao.selectConsultationsByMemberId(memberId);
        List<ConsultationResponseDto> listResponse = new ArrayList<>();

        for(Consultation consultation : consultations) {
            ConsultationResponseDto response = new ConsultationResponseDto();
            response.setConsultationId(consultation.getConsultationId());
            response.setMemberId(consultation.getMemberId());
            response.setReservationId(consultation.getReservationId());
            response.setTreatmentId(consultation.getTreatmentId());
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
    public ConsultationResponseDto getConsultationById(Integer consultationId, Integer memberId) {

        if(consultationId == null) {
            throw new IllegalArgumentException("상담 ID는 필수입니다.");
        }

        if(memberId == null) {
            throw new IllegalArgumentException("회원 ID는 필수입니다.");
        }

        Consultation consultation = consultationDao.selectConsultationById(consultationId, memberId);

        if(consultation == null) {
            throw new IllegalArgumentException("존재하지 않는 상담입니다.");
        }

        ConsultationResponseDto response = new ConsultationResponseDto();
        response.setConsultationId(consultation.getConsultationId());
        response.setMemberId(consultation.getMemberId());
        response.setReservationId(consultation.getReservationId());
        response.setTreatmentId(consultation.getTreatmentId());
        response.setConsultationStatus(consultation.getConsultationStatus());
        response.setConsultationMemo(consultation.getConsultationMemo());
        response.setPreferredDate(consultation.getPreferredDate());
        response.setPreferredTime(consultation.getPreferredTime());
        response.setCreatedAt(consultation.getCreatedAt());
        response.setUpdatedAt(consultation.getUpdatedAt());

        return response;
    }

    // 상담 정보 수정
    public int modifyConsultation(ConsultationUpdateRequestDto request) {

        if(request == null) {
            throw new IllegalArgumentException("상담 수정 요청 정보가 없습니다.");
        }

        if(request.getConsultationId() == null) {
            throw new IllegalArgumentException("상담 ID는 필수입니다.");
        }

        if(request.getMemberId() == null) {
            throw new IllegalArgumentException("회원 ID는 필수입니다.");
        }

        if(request.getTreatmentId() == null) {
            throw new IllegalArgumentException("진료 항목 ID는 필수입니다.");
        }

        Consultation existingConsultation = consultationDao.selectConsultationById(request.getConsultationId(), request.getMemberId());

        if(existingConsultation == null) {
            throw new IllegalArgumentException("존재하지 않는 상담입니다.");
        }

        Consultation consultation = new Consultation();
        consultation.setConsultationId(request.getConsultationId());
        consultation.setMemberId(request.getMemberId());
        consultation.setTreatmentId(request.getTreatmentId());
        consultation.setConsultationMemo(request.getConsultationMemo());
        consultation.setPreferredDate(request.getPreferredDate());
        consultation.setPreferredTime(request.getPreferredTime());

        return consultationDao.updateConsultation(consultation);
    }

    // 상담 취소
    public int cancelConsultation(ConsultationCancelRequestDto request) {

        if(request == null) {
            throw new IllegalArgumentException("상담 취소 요청 정보가 없습니다.");
        }

        if(request.getConsultationId() == null) {
            throw new IllegalArgumentException("상담 ID는 필수입니다.");
        }

        if(request.getMemberId() == null) {
            throw new IllegalArgumentException("회원 ID는 필수입니다.");
        }

        Consultation existingConsultation = consultationDao.selectConsultationById(request.getConsultationId(), request.getMemberId());

        if(existingConsultation == null) {
            throw new IllegalArgumentException("존재하지 않는 상담입니다.");
        }

        return consultationDao.cancelConsultation(request.getConsultationId(), request.getMemberId());
    }
}
