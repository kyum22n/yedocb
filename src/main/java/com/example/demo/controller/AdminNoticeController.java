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

import com.example.demo.dto.request.notice.AdminNoticeCreateRequestDto;
import com.example.demo.dto.request.notice.AdminNoticeUpdateRequestDto;
import com.example.demo.dto.response.notice.AdminNoticeResponseDto;
import com.example.demo.service.AdminNoticeService;

/**
 * 파일명: AdminNoticeController.java
 * 설명: 관리자용 공지/이벤트 관련 controller
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-25 | 규민 | 클래스 생성
 */

@RestController
@RequestMapping("/admin/notices")
public class AdminNoticeController {

    @Autowired
    private AdminNoticeService noticeService;

    // 공지/이벤트 등록
    @PostMapping("/register")
    public ResponseEntity<Integer> registerNotice(@RequestBody AdminNoticeCreateRequestDto request) {
        return ResponseEntity.ok(noticeService.createNotice(request));
    }

    // 공지/이벤트 목록 조회
    @GetMapping("/all")
    public ResponseEntity<List<AdminNoticeResponseDto>> getNoticeList() {
        return ResponseEntity.ok(noticeService.getAllNotices());
    }

    // 공지/이벤트 유형별 목록 조회
    @GetMapping("/type")
    public ResponseEntity<List<AdminNoticeResponseDto>> getNoticesByType(
            @RequestParam("noticeType") String noticeType) {
        return ResponseEntity.ok(noticeService.getNoticesByType(noticeType));
    }

    // 공지/이벤트 상세 조회
    @GetMapping("/{noticeId}")
    public ResponseEntity<AdminNoticeResponseDto> getNoticeDetail(@PathVariable("noticeId") Integer noticeId) {
        return ResponseEntity.ok(noticeService.getNoticeById(noticeId));
    }

    // 공지/이벤트 수정
    @PutMapping("/update")
    public ResponseEntity<Integer> updateNotice(@RequestBody AdminNoticeUpdateRequestDto request) {
        return ResponseEntity.ok(noticeService.modifyNotice(request));
    }

    // 공지/이벤트 삭제
    @DeleteMapping("/delete/{noticeId}")
    public ResponseEntity<Integer> deleteNotice(@PathVariable("noticeId") Integer noticeId) {
        return ResponseEntity.ok(noticeService.removeNotice(noticeId));
    }
}
