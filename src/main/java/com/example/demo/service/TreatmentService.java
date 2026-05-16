package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.TreatmentDao;
import com.example.demo.dto.response.treatment.TreatmentResponseDto;
import com.example.demo.entity.Treatment;
import java.util.ArrayList;
import java.util.List;

/**
 * 파일명: TreatmentService.java
 * 설명: 진료항목 관련 서비스 클래스 (사용자용)
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 클래스 생성
 */

@Service
public class TreatmentService {

    @Autowired
    private TreatmentDao treatmentDao;

    // 진료항목 목록 조회
    public List<TreatmentResponseDto> getVisibleTreatments() {

        List<Treatment> treatments = treatmentDao.selectVisibleTreatments();
        List<TreatmentResponseDto> listResponse = new ArrayList<>();

        for (Treatment treatment : treatments) {
            TreatmentResponseDto response = new TreatmentResponseDto();
            response.setTreatmentId(treatment.getTreatmentId());
            response.setTreatmentName(treatment.getTreatmentName());
            response.setDescription(treatment.getDescription());
            
            listResponse.add(response);
        }

        return listResponse;
    }

    // 카테고리별 진료항목 조회
    public List<TreatmentResponseDto> getVisibleTreatmentsByCategoryId(Integer categoryId) {
        List<Treatment> treatments = treatmentDao.selectVisibleTreatmentsByCategoryId(categoryId);
        List<TreatmentResponseDto> listResponse = new ArrayList<>();

        for(Treatment treatment : treatments) {
            TreatmentResponseDto response = new TreatmentResponseDto();
            response.setTreatmentId(treatment.getTreatmentId());
            response.setTreatmentName(treatment.getTreatmentName());
            response.setDescription(treatment.getDescription());

            listResponse.add(response);
        }

        return listResponse;
    }

    // 진료항목 단일 조회
    public TreatmentResponseDto getVisibleTreatmentById(Integer treatmentId) {

        Treatment treatment = treatmentDao.selectVisibleTreatmentById(treatmentId);

        if(treatment == null) {
            throw new IllegalArgumentException("존재하지 않는 항목입니다.");
        }

        TreatmentResponseDto response = new TreatmentResponseDto();
        response.setTreatmentId(treatment.getTreatmentId());
        response.setTreatmentName(treatment.getTreatmentName());
        response.setDescription(treatment.getDescription());

        return response;
    }
}
