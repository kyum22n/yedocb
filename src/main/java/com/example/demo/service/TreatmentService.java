package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.TreatmentDao;
import com.example.demo.dto.response.treatment.TreatmentResponseDto;
import com.example.demo.entity.Treatment;
import com.example.demo.exception.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 파일명: TreatmentService.java
 * 설명: 진료항목 관련 서비스 클래스 (사용자용)
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 클래스 생성
 * 2026-09-06 | 리팩토링 | "존재하지 않는 항목입니다" -> ResourceNotFoundException 교체,
 *                        응답 DTO 매핑을 TreatmentResponseDto.from(entity) 정적 팩토리 방식으로 변경 (Phase 2)
 */

@Service
public class TreatmentService {

    @Autowired
    private TreatmentDao treatmentDao;

    // 진료항목 목록 조회
    public List<TreatmentResponseDto> getVisibleTreatments() {
        return treatmentDao.selectVisibleTreatments().stream()
                .map(TreatmentResponseDto::from)
                .collect(Collectors.toList());
    }

    // 카테고리별 진료항목 조회
    public List<TreatmentResponseDto> getVisibleTreatmentsByCategoryId(Integer categoryId) {
        return treatmentDao.selectVisibleTreatmentsByCategoryId(categoryId).stream()
                .map(TreatmentResponseDto::from)
                .collect(Collectors.toList());
    }

    // 진료항목 단일 조회
    public TreatmentResponseDto getVisibleTreatmentById(Integer treatmentId) {

        Treatment treatment = treatmentDao.selectVisibleTreatmentById(treatmentId);

        if(treatment == null) {
            throw new ResourceNotFoundException("존재하지 않는 항목입니다.");
        }

        return TreatmentResponseDto.from(treatment);
    }
}
