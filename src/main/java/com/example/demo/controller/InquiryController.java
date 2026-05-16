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

import com.example.demo.dto.request.inquiry.InquiryCreateRequestDto;
import com.example.demo.dto.request.inquiry.InquiryDeleteRequestDto;
import com.example.demo.dto.request.inquiry.InquiryUpdateRequestDto;
import com.example.demo.dto.response.inquiry.InquiryResponseDto;
import com.example.demo.service.InquiryService;

/**
 * 파일명: InquiryController.java
 * 설명: 사용자용 문의 관련 controller
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 클래스 생성
 */

@RestController
@RequestMapping("/inquiries")
public class InquiryController {

    @Autowired
    private InquiryService inquiryService;

    // 문의 등록
    @PostMapping("/register")
    public ResponseEntity<Integer> registerInquiry(@RequestBody InquiryCreateRequestDto request) {
        return ResponseEntity.ok(inquiryService.createInquiry(request));
    }

    // 회원별 문의 목록 조회
    @GetMapping("/member")
    public ResponseEntity<List<InquiryResponseDto>> getInquiriesByMemberId(
            @RequestParam("memberId") Integer memberId) {
        return ResponseEntity.ok(inquiryService.getInquiriesByMemberId(memberId));
    }

    // 문의 상세 조회
    @GetMapping("/{inquiryId}")
    public ResponseEntity<InquiryResponseDto> getInquiryDetail(
            @PathVariable("inquiryId") Integer inquiryId,
            @RequestParam("memberId") Integer memberId) {
        return ResponseEntity.ok(inquiryService.getInquiryById(inquiryId, memberId));
    }

    // 문의 수정
    @PutMapping("/update")
    public ResponseEntity<Integer> updateInquiry(@RequestBody InquiryUpdateRequestDto request) {
        return ResponseEntity.ok(inquiryService.modifyInquiry(request));
    }

    // 문의 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<Integer> deleteInquiry(@RequestBody InquiryDeleteRequestDto request) {
        return ResponseEntity.ok(inquiryService.removeInquiry(request));
    }
}
