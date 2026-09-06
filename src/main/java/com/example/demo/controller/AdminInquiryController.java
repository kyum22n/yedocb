package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.inquiry.AdminInquiryAnswerCreateRequestDto;
import com.example.demo.dto.request.inquiry.AdminInquiryAnswerUpdateRequestDto;
import com.example.demo.dto.request.inquiry.AdminInquiryStatusUpdateRequestDto;
import com.example.demo.dto.response.inquiry.AdminInquiryResponseDto;
import com.example.demo.service.AdminInquiryService;

/**
 * 파일명: AdminInquiryController.java
 * 설명: 관리자용 문의 관련 controller
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 클래스 생성
 */

@RestController
@RequestMapping("/admin/inquiries")
public class AdminInquiryController {

    @Autowired
    private AdminInquiryService inquiryService;

    // 문의 목록 조회
    @GetMapping("/all")
    public ResponseEntity<List<AdminInquiryResponseDto>> getInquiryList() {
        return ResponseEntity.ok(inquiryService.getAllInquiries());
    }

    // 회원별 문의 목록 조회
    @GetMapping("/member")
    public ResponseEntity<List<AdminInquiryResponseDto>> getInquiriesByUId(
            @RequestParam("uId") String uId) {
        return ResponseEntity.ok(inquiryService.getInquiriesByUId(uId));
    }

    // 문의 유형별 목록 조회
    @GetMapping("/type")
    public ResponseEntity<List<AdminInquiryResponseDto>> getInquiriesByType(
            @RequestParam("inquiryType") String inquiryType) {
        return ResponseEntity.ok(inquiryService.getInquiriesByType(inquiryType));
    }

    // 문의 상태별 목록 조회
    @GetMapping("/status")
    public ResponseEntity<List<AdminInquiryResponseDto>> getInquiriesByStatus(
            @RequestParam("inquiryStatus") String inquiryStatus) {
        return ResponseEntity.ok(inquiryService.getInquiriesByStatus(inquiryStatus));
    }

    // 문의 상세 조회
    @GetMapping("/{inquiryId}")
    public ResponseEntity<AdminInquiryResponseDto> getInquiryDetail(@PathVariable("inquiryId") Integer inquiryId) {
        return ResponseEntity.ok(inquiryService.getInquiryById(inquiryId));
    }

    // 문의 상태 수정
    @PutMapping("/status/update")
    public ResponseEntity<Integer> updateInquiryStatus(@RequestBody AdminInquiryStatusUpdateRequestDto request) {
        return ResponseEntity.ok(inquiryService.modifyInquiryStatus(request));
    }

    // 답변 등록
    @PostMapping("/answers/register")
    public ResponseEntity<Integer> registerInquiryAnswer(@RequestBody AdminInquiryAnswerCreateRequestDto request) {
        return ResponseEntity.ok(inquiryService.createInquiryAnswer(request));
    }

    // 답변 수정
    @PutMapping("/answers/update")
    public ResponseEntity<Integer> updateInquiryAnswer(@RequestBody AdminInquiryAnswerUpdateRequestDto request) {
        return ResponseEntity.ok(inquiryService.modifyInquiryAnswer(request));
    }

    // 답변 삭제
    @DeleteMapping("/answers/delete/{answerId}")
    public ResponseEntity<Integer> deleteInquiryAnswer(@PathVariable("answerId") Integer answerId) {
        return ResponseEntity.ok(inquiryService.removeInquiryAnswer(answerId));
    }

    // 문의 삭제
    @DeleteMapping("/delete/{inquiryId}")
    public ResponseEntity<Integer> deleteInquiry(@PathVariable("inquiryId") Integer inquiryId) {
        return ResponseEntity.ok(inquiryService.removeInquiry(inquiryId));
    }
}
