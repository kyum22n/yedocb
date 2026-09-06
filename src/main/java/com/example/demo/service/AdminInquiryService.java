package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.AdminInquiryDao;
import com.example.demo.dto.request.inquiry.AdminInquiryAnswerCreateRequestDto;
import com.example.demo.dto.request.inquiry.AdminInquiryAnswerUpdateRequestDto;
import com.example.demo.dto.request.inquiry.AdminInquiryStatusUpdateRequestDto;
import com.example.demo.dto.response.inquiry.AdminInquiryResponseDto;
import com.example.demo.dto.response.inquiry.InquiryAnswerResponseDto;
import com.example.demo.entity.Inquiry;
import com.example.demo.entity.InquiryAnswer;

/**
 * 파일명: AdminInquiryService.java
 * 설명: 관리자용 문의 관련 서비스 클래스
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 클래스 생성
 */

@Service
public class AdminInquiryService {

    @Autowired
    private AdminInquiryDao adminInquiryDao;

    // 문의 목록 조회
    public List<AdminInquiryResponseDto> getAllInquiries() {

        List<Inquiry> inquiries = adminInquiryDao.selectAllInquiries();
        List<AdminInquiryResponseDto> listResponse = new ArrayList<>();

        for(Inquiry inquiry : inquiries) {
            AdminInquiryResponseDto response = new AdminInquiryResponseDto();
            response.setInquiryId(inquiry.getInquiryId());
            response.setUId(inquiry.getUId());
            response.setInquiryType(inquiry.getInquiryType());
            response.setTitle(inquiry.getTitle());
            response.setContent(inquiry.getContent());
            response.setInquiryStatus(inquiry.getInquiryStatus());
            response.setCreatedAt(inquiry.getCreatedAt());
            response.setUpdatedAt(inquiry.getUpdatedAt());

            InquiryAnswer answer = adminInquiryDao.selectAnswerByInquiryId(inquiry.getInquiryId());
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

    // 회원별 문의 목록 조회
    public List<AdminInquiryResponseDto> getInquiriesByUId(String uId) {

        if(uId == null) {
            throw new IllegalArgumentException("회원 ID는 필수입니다.");
        }

        List<Inquiry> inquiries = adminInquiryDao.selectInquiriesByUId(uId);
        List<AdminInquiryResponseDto> listResponse = new ArrayList<>();

        for(Inquiry inquiry : inquiries) {
            AdminInquiryResponseDto response = new AdminInquiryResponseDto();
            response.setInquiryId(inquiry.getInquiryId());
            response.setUId(inquiry.getUId());
            response.setInquiryType(inquiry.getInquiryType());
            response.setTitle(inquiry.getTitle());
            response.setContent(inquiry.getContent());
            response.setInquiryStatus(inquiry.getInquiryStatus());
            response.setCreatedAt(inquiry.getCreatedAt());
            response.setUpdatedAt(inquiry.getUpdatedAt());

            InquiryAnswer answer = adminInquiryDao.selectAnswerByInquiryId(inquiry.getInquiryId());
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

    // 문의 유형별 목록 조회
    public List<AdminInquiryResponseDto> getInquiriesByType(String inquiryType) {

        if(inquiryType == null || inquiryType.isBlank()) {
            throw new IllegalArgumentException("문의 유형은 필수입니다.");
        }

        List<Inquiry> inquiries = adminInquiryDao.selectInquiriesByType(inquiryType);
        List<AdminInquiryResponseDto> listResponse = new ArrayList<>();

        for(Inquiry inquiry : inquiries) {
            AdminInquiryResponseDto response = new AdminInquiryResponseDto();
            response.setInquiryId(inquiry.getInquiryId());
            response.setUId(inquiry.getUId());
            response.setInquiryType(inquiry.getInquiryType());
            response.setTitle(inquiry.getTitle());
            response.setContent(inquiry.getContent());
            response.setInquiryStatus(inquiry.getInquiryStatus());
            response.setCreatedAt(inquiry.getCreatedAt());
            response.setUpdatedAt(inquiry.getUpdatedAt());

            listResponse.add(response);
        }

        return listResponse;
    }

    // 문의 상태별 목록 조회
    public List<AdminInquiryResponseDto> getInquiriesByStatus(String inquiryStatus) {

        if(inquiryStatus == null || inquiryStatus.isBlank()) {
            throw new IllegalArgumentException("문의 상태는 필수입니다.");
        }

        if(!inquiryStatus.equals("WAITING") && !inquiryStatus.equals("ANSWERED")) {
            throw new IllegalArgumentException("올바르지 않은 문의 상태입니다.");
        }

        List<Inquiry> inquiries = adminInquiryDao.selectInquiriesByStatus(inquiryStatus);
        List<AdminInquiryResponseDto> listResponse = new ArrayList<>();

        for(Inquiry inquiry : inquiries) {
            AdminInquiryResponseDto response = new AdminInquiryResponseDto();
            response.setInquiryId(inquiry.getInquiryId());
            response.setUId(inquiry.getUId());
            response.setInquiryType(inquiry.getInquiryType());
            response.setTitle(inquiry.getTitle());
            response.setContent(inquiry.getContent());
            response.setInquiryStatus(inquiry.getInquiryStatus());
            response.setCreatedAt(inquiry.getCreatedAt());
            response.setUpdatedAt(inquiry.getUpdatedAt());

            listResponse.add(response);
        }

        return listResponse;
    }

    // 문의 상세 조회
    public AdminInquiryResponseDto getInquiryById(Integer inquiryId) {

        if(inquiryId == null) {
            throw new IllegalArgumentException("문의 ID는 필수입니다.");
        }

        Inquiry inquiry = adminInquiryDao.selectInquiryById(inquiryId);

        if(inquiry == null) {
            throw new ResourceNotFoundException("존재하지 않는 문의입니다.");
        }

        AdminInquiryResponseDto response = new AdminInquiryResponseDto();
        response.setInquiryId(inquiry.getInquiryId());
        response.setUId(inquiry.getUId());
        response.setInquiryType(inquiry.getInquiryType());
        response.setTitle(inquiry.getTitle());
        response.setContent(inquiry.getContent());
        response.setInquiryStatus(inquiry.getInquiryStatus());
        response.setCreatedAt(inquiry.getCreatedAt());
        response.setUpdatedAt(inquiry.getUpdatedAt());

        InquiryAnswer answer = adminInquiryDao.selectAnswerByInquiryId(inquiry.getInquiryId());
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

    // 문의 상태 수정
    public int modifyInquiryStatus(AdminInquiryStatusUpdateRequestDto request) {

        if(request == null) {
            throw new IllegalArgumentException("문의 상태 수정 요청 정보가 없습니다.");
        }

        if(request.getInquiryId() == null) {
            throw new IllegalArgumentException("문의 ID는 필수입니다.");
        }

        if(request.getInquiryStatus() == null || request.getInquiryStatus().isBlank()) {
            throw new IllegalArgumentException("문의 상태는 필수입니다.");
        }

        if(!request.getInquiryStatus().equals("WAITING") && !request.getInquiryStatus().equals("ANSWERED")) {
            throw new IllegalArgumentException("올바르지 않은 문의 상태입니다.");
        }

        Inquiry existingInquiry = adminInquiryDao.selectInquiryById(request.getInquiryId());

        if(existingInquiry == null) {
            throw new ResourceNotFoundException("존재하지 않는 문의입니다.");
        }

        Inquiry inquiry = new Inquiry();
        inquiry.setInquiryId(request.getInquiryId());
        inquiry.setInquiryStatus(request.getInquiryStatus());

        return adminInquiryDao.updateInquiryStatus(inquiry);
    }

    // 답변 등록
    public int createInquiryAnswer(AdminInquiryAnswerCreateRequestDto request) {

        if(request == null) {
            throw new IllegalArgumentException("답변 등록 요청 정보가 없습니다.");
        }

        if(request.getInquiryId() == null) {
            throw new IllegalArgumentException("문의 ID는 필수입니다.");
        }

        if(request.getAdminId() == null) {
            throw new IllegalArgumentException("관리자 ID는 필수입니다.");
        }

        if(request.getAnswerContent() == null || request.getAnswerContent().isBlank()) {
            throw new IllegalArgumentException("답변 내용은 필수입니다.");
        }

        Inquiry existingInquiry = adminInquiryDao.selectInquiryById(request.getInquiryId());

        if(existingInquiry == null) {
            throw new ResourceNotFoundException("존재하지 않는 문의입니다.");
        }

        InquiryAnswer inquiryAnswer = new InquiryAnswer();
        inquiryAnswer.setInquiryId(request.getInquiryId());
        inquiryAnswer.setAdminId(request.getAdminId());
        inquiryAnswer.setAnswerContent(request.getAnswerContent());

        int result = adminInquiryDao.insertInquiryAnswer(inquiryAnswer);

        Inquiry inquiry = new Inquiry();
        inquiry.setInquiryId(request.getInquiryId());
        inquiry.setInquiryStatus("ANSWERED");
        adminInquiryDao.updateInquiryStatus(inquiry);

        return result;
    }

    // 답변 수정
    public int modifyInquiryAnswer(AdminInquiryAnswerUpdateRequestDto request) {

        if(request == null) {
            throw new IllegalArgumentException("답변 수정 요청 정보가 없습니다.");
        }

        if(request.getAnswerId() == null) {
            throw new IllegalArgumentException("답변 ID는 필수입니다.");
        }

        if(request.getAnswerContent() == null || request.getAnswerContent().isBlank()) {
            throw new IllegalArgumentException("답변 내용은 필수입니다.");
        }

        InquiryAnswer existingAnswer = adminInquiryDao.selectAnswerById(request.getAnswerId());

        if(existingAnswer == null) {
            throw new ResourceNotFoundException("존재하지 않는 답변입니다.");
        }

        InquiryAnswer inquiryAnswer = new InquiryAnswer();
        inquiryAnswer.setAnswerId(request.getAnswerId());
        inquiryAnswer.setAnswerContent(request.getAnswerContent());

        return adminInquiryDao.updateInquiryAnswer(inquiryAnswer);
    }

    // 답변 삭제
    public int removeInquiryAnswer(Integer answerId) {

        if(answerId == null) {
            throw new IllegalArgumentException("답변 ID는 필수입니다.");
        }

        InquiryAnswer existingAnswer = adminInquiryDao.selectAnswerById(answerId);

        if(existingAnswer == null) {
            throw new ResourceNotFoundException("존재하지 않는 답변입니다.");
        }

        int result = adminInquiryDao.deleteInquiryAnswer(answerId);

        Inquiry inquiry = new Inquiry();
        inquiry.setInquiryId(existingAnswer.getInquiryId());
        inquiry.setInquiryStatus("WAITING");
        adminInquiryDao.updateInquiryStatus(inquiry);

        return result;
    }

    // 문의 삭제
    public int removeInquiry(Integer inquiryId) {

        if(inquiryId == null) {
            throw new IllegalArgumentException("문의 ID는 필수입니다.");
        }

        Inquiry existingInquiry = adminInquiryDao.selectInquiryById(inquiryId);

        if(existingInquiry == null) {
            throw new ResourceNotFoundException("존재하지 않는 문의입니다.");
        }

        adminInquiryDao.deleteAnswerByInquiryId(inquiryId);

        return adminInquiryDao.deleteInquiry(inquiryId);
    }
}
