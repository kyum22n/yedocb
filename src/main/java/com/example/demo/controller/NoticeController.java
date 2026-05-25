package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.response.notice.NoticeResponseDto;
import com.example.demo.service.NoticeService;

/**
 * 파일명: NoticeController.java
 * 설명: 사용자용 공지/이벤트 관련 controller
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-25 | 규민 | 클래스 생성
 */

@RestController
@RequestMapping("/notices")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    // 노출 공지/이벤트 목록 조회
    @GetMapping("/all")
    public ResponseEntity<List<NoticeResponseDto>> getVisibleNoticeList() {
        return ResponseEntity.ok(noticeService.getVisibleNotices());
    }

    // 노출 공지/이벤트 유형별 목록 조회
    @GetMapping("/type")
    public ResponseEntity<List<NoticeResponseDto>> getVisibleNoticesByType(
            @RequestParam("noticeType") String noticeType) {
        return ResponseEntity.ok(noticeService.getVisibleNoticesByType(noticeType));
    }

    // 노출 공지/이벤트 상세 조회
    @GetMapping("/{noticeId}")
    public ResponseEntity<NoticeResponseDto> getVisibleNoticeDetail(@PathVariable("noticeId") Integer noticeId) {
        return ResponseEntity.ok(noticeService.getVisibleNoticeById(noticeId));
    }
}
