package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.AdminTreatmentDao;
import com.example.demo.dto.request.treatment.TreatmentUpdateRequestDto;
import com.example.demo.dto.response.treatment.AdminTreatmentResponseDto;
import com.example.demo.dto.request.treatment.TreatmentCreateRequestDto;
import com.example.demo.entity.Treatment;
import com.example.demo.exception.ResourceNotFoundException;

/**
 * 파일명: AdminTreatmentService.java
 * 설명: 관리자용 진료항목 관련 서비스 클래스
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 클래스 생성
 * 2026-09-06 | 리팩토링 | "존재하지 않는 항목입니다" -> ResourceNotFoundException 교체,
 *                        응답 DTO 매핑을 AdminTreatmentResponseDto.from(entity) 정적 팩토리 방식으로 변경 (Phase 2)
 */

@Service
public class AdminTreatmentService {

    @Autowired
    private AdminTreatmentDao treatmentDao;

    // 진료항목 목록 조회
    public List<AdminTreatmentResponseDto> getAllTreatments() {
        return treatmentDao.selectAllTreatments().stream()
                .map(AdminTreatmentResponseDto::from)
                .collect(Collectors.toList());
    }

    // 카테고리별 항목 조회
    public List<AdminTreatmentResponseDto> getTreatmentsByCategoryId(Integer categoryId) {
        return treatmentDao.selectTreatmentsByCategoryId(categoryId).stream()
                .map(AdminTreatmentResponseDto::from)
                .collect(Collectors.toList());
    }

    // 진료 항목 조회
    public AdminTreatmentResponseDto getTreatmentById(Integer treatmentId){

        Treatment treatment = treatmentDao.selectTreatmentById(treatmentId);

        if (treatment == null) {
            throw new ResourceNotFoundException("존재하지 않는 항목입니다.");
        }

        return AdminTreatmentResponseDto.from(treatment);
    }

    // 진료 항목 추가
    public int createTreatment(TreatmentCreateRequestDto request) {

        Treatment treatment = new Treatment();

        treatment.setCategoryId(request.getCategoryId());
        treatment.setTreatmentName(request.getTreatmentName());
        treatment.setDescription(request.getDescription());
        treatment.setIsReservable(request.getIsReservable() != null ? request.getIsReservable() : true);
        treatment.setIsVisible(request.getIsVisible() != null ? request.getIsVisible() : true);

        return treatmentDao.insertTreatment(treatment);
    }

    // 진료 항목 수정
    public int updateTreatment(TreatmentUpdateRequestDto request) {

        Treatment existingTreatment = treatmentDao.selectTreatmentById(request.getTreatmentId());

        if(existingTreatment == null) {
            throw new ResourceNotFoundException("존재하지 않는 항목입니다.");
        }

        Treatment treatment = new Treatment();

        treatment.setTreatmentId(request.getTreatmentId());
        treatment.setCategoryId(request.getCategoryId());
        treatment.setTreatmentName(request.getTreatmentName());
        treatment.setDescription(request.getDescription());
        treatment.setIsReservable(request.getIsReservable() != null ? request.getIsReservable() : true);
        treatment.setIsVisible(request.getIsVisible() != null ? request.getIsVisible() : true);

        return treatmentDao.updateTreatment(treatment);
    }

    // 진료 항목 삭제
    public int deleteTreatment(Integer treatmentId) {

        Treatment existingTreatment = treatmentDao.selectTreatmentById(treatmentId);

        if(existingTreatment == null) {
            throw new ResourceNotFoundException("존재하지 않는 항목입니다.");
        }

        return treatmentDao.deleteTreatment(treatmentId);
    }
}
