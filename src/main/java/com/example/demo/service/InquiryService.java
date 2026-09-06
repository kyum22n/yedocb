package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.InquiryDao;
import com.example.demo.dto.request.inquiry.InquiryCreateRequestDto;
import com.example.demo.dto.request.inquiry.InquiryDeleteRequestDto;
import com.example.demo.dto.request.inquiry.InquiryUpdateRequestDto;
import com.example.demo.dto.response.inquiry.InquiryAnswerResponseDto;
import com.example.demo.dto.response.inquiry.InquiryResponseDto;
import com.example.demo.entity.Inquiry;
import com.example.demo.entity.InquiryAnswer;

/**
 * 파일명: InquiryService.java
 * 설명: 사용자용 문의 관련 서비스 클래스
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 클래스 생성
 */

@Service
public class InquiryService {

    @Autowired
    private InquiryDao inquiryDao;

    // 문의 등록
    public int createInquiry(InquiryCreateRequestDto request) {

        if(request == null) {
            throw new IllegalArgumentException("문의 등록 요청 정보가 없습니다.");
        }

        if(request.getUId() == null) {
            throw new IllegalArgumentException("회원 ID는 필수입니다.");
        }

        if(request.getInquiryType() == null || request.getInquiryType().isBlank()) {
            throw new IllegalArgumentException("문의 유형은 필수입니다.");
        }

        if(!request.getInquiryType().equals("RESERVATION")
                && !request.getInquiryType().equals("TREATMENT")
                && !request.getInquiryType().equals("PAYMENT")
                && !request.getInquiryType().equals("ETC")) {
            throw new IllegalArgumentException("올바르지 않은 문의 유형입니다.");
        }

        if(request.getTitle() == null || request.getTitle().isBlank()) {
            throw new IllegalArgumentException("문의 제목은 필수입니다.");
        }

        if(request.getContent() == null || request.getContent().isBlank()) {
            throw new IllegalArgumentException("문의 내용은 필수입니다.");
        }

        Inquiry inquiry = new Inquiry();
        inquiry.setUId(request.getUId());
        inquiry.setInquiryType(request.getInquiryType());
        inquiry.setTitle(request.getTitle());
        inquiry.setContent(request.getContent());

        return inquiryDao.insertInquiry(inquiry);
    }

    // 회원별 문의 목록 조회
    public List<InquiryResponseDto> getInquiriesByUId(String uId) {

        if(uId == null) {
            throw new IllegalArgumentException("회원 ID는 필수입니다.");
        }

        List<Inquiry> inquiries = inquiryDao.selectInquiriesByUId(uId);
        List<InquiryResponseDto> listResponse = new ArrayList<>();

        for(Inquiry inquiry : inquiries) {
            InquiryResponseDto response = new InquiryResponseDto();
            response.setInquiryId(inquiry.getInquiryId());
            response.setUId(inquiry.getUId());
            response.setInquiryType(inquiry.getInquiryType());
            response.setTitle(inquiry.getTitle());
            response.setContent(inquiry.getContent());
            response.setInquiryStatus(inquiry.getInquiryStatus());
            response.setCreatedAt(inquiry.getCreatedAt());
            response.setUpdatedAt(inquiry.getUpdatedAt());

            InquiryAnswer answer = inquiryDao.selectAnswerByInquiryId(inquiry.getInquiryId());
            if(answer != null) {
                InquiryAnswerResponseDto answerResponse = new InquiryAnswerResponseDto();
                answerResponse.setAnswerId(answer.getAnswerId());
                answerResponse.setInquiryId(answer.getInquiryId());
                answerResponse.setAdminId(answer.getAdminId());
                answerResponse.setAnswerContent(answer.getAnswerContent());
                answerResponse.setCreatedAt(answer.getCreatedAt());
                answerResponse.setUpdatedAt(answer.getUpdatedAt());
                response.setAnswer(answerResponse);
            }

            listResponse.add(response);
        }

        return listResponse;
    }

    // 문의 상세 조회
    public InquiryResponseDto getInquiryById(Integer inquiryId, String uId) {

        if(inquiryId == null) {
            throw new IllegalArgumentException("문의 ID는 필수입니다.");
        }

        if(uId == null) {
            throw new IllegalArgumentException("회원 ID는 필수입니다.");
        }

        Inquiry inquiry = inquiryDao.selectInquiryById(inquiryId, uId);

        if(inquiry == null) {
            throw new ResourceNotFoundException("존재하지 않는 문의입니다.");
        }

        InquiryResponseDto response = new InquiryResponseDto();
        response.setInquiryId(inquiry.getInquiryId());
        response.setUId(inquiry.getUId());
        response.setInquiryType(inquiry.getInquiryType());
        response.setTitle(inquiry.getTitle());
        response.setContent(inquiry.getContent());
        response.setInquiryStatus(inquiry.getInquiryStatus());
        response.setCreatedAt(inquiry.getCreatedAt());
        response.setUpdatedAt(inquiry.getUpdatedAt());

        InquiryAnswer answer = inquiryDao.selectAnswerByInquiryId(inquiry.getInquiryId());
        if(answer != null) {
            InquiryAnswerResponseDto answerResponse = new InquiryAnswerResponseDto();
            answerResponse.setAnswerId(answer.getAnswerId());
            answerResponse.setInquiryId(answer.getInquiryId());
            answerResponse.setAdminId(answer.getAdminId());
            answerResponse.setAnswerContent(answer.getAnswerContent());
            answerResponse.setCreatedAt(answer.getCreatedAt());
            answerResponse.setUpdatedAt(answer.getUpdatedAt());
            response.setAnswer(answerResponse);
        }

        return response;
    }

    // 문의 수정
    public int modifyInquiry(InquiryUpdateRequestDto request) {

        if(request == null) {
            throw new IllegalArgumentException("문의 수정 요청 정보가 없습니다.");
        }

        if(request.getInquiryId() == null) {
            throw new IllegalArgumentException("문의 ID는 필수입니다.");
        }

        if(request.getUId() == null) {
            throw new IllegalArgumentException("회원 ID는 필수입니다.");
        }

        if(request.getInquiryType() == null || request.getInquiryType().isBlank()) {
            throw new IllegalArgumentException("문의 유형은 필수입니다.");
        }

        if(!request.getInquiryType().equals("RESERVATION")
                && !request.getInquiryType().equals("TREATMENT")
                && !request.getInquiryType().equals("PAYMENT")
                && !request.getInquiryType().equals("ETC")) {
            throw new IllegalArgumentException("올바르지 않은 문의 유형입니다.");
        }

        Inquiry existingInquiry = inquiryDao.selectInquiryById(request.getInquiryId(), request.getUId());

        if(existingInquiry == null) {
            throw new ResourceNotFoundException("존재하지 않는 문의입니다.");
        }

        Inquiry inquiry = new Inquiry();
        inquiry.setInquiryId(request.getInquiryId());
        inquiry.setUId(request.getUId());
        inquiry.setInquiryType(request.getInquiryType());
        inquiry.setTitle(request.getTitle());
        inquiry.setContent(request.getContent());

        return inquiryDao.updateInquiry(inquiry);
    }

    // 문의 삭제
    public int removeInquiry(InquiryDeleteRequestDto request) {

        if(request == null) {
            throw new IllegalArgumentException("문의 삭제 요청 정보가 없습니다.");
        }

        if(request.getInquiryId() == null) {
            throw new IllegalArgumentException("문의 ID는 필수입니다.");
        }

        if(request.getUId() == null) {
            throw new IllegalArgumentException("회원 ID는 필수입니다.");
        }

        Inquiry existingInquiry = inquiryDao.selectInquiryById(request.getInquiryId(), request.getUId());

        if(existingInquiry == null) {
            throw new ResourceNotFoundException("존재하지 않는 문의입니다.");
        }

        inquiryDao.deleteAnswerByInquiryId(request.getInquiryId());

        return inquiryDao.deleteInquiry(request.getInquiryId(), request.getUId());
    }
}
