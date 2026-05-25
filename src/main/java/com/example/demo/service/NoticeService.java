package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.NoticeDao;
import com.example.demo.dto.response.notice.NoticeResponseDto;
import com.example.demo.entity.Notice;

/**
 * 파일명: NoticeService.java
 * 설명: 사용자용 공지/이벤트 관련 서비스 클래스
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-25 | 규민 | 클래스 생성
 */

@Service
public class NoticeService {

    @Autowired
    private NoticeDao noticeDao;

    // 노출 공지/이벤트 목록 조회
    public List<NoticeResponseDto> getVisibleNotices() {

        List<Notice> notices = noticeDao.selectVisibleNotices();
        List<NoticeResponseDto> listResponse = new ArrayList<>();

        for(Notice notice : notices) {
            NoticeResponseDto response = new NoticeResponseDto();
            response.setNoticeId(notice.getNoticeId());
            response.setTitle(notice.getTitle());
            response.setContent(notice.getContent());
            response.setNoticeType(notice.getNoticeType());
            response.setImageUrl(notice.getImageUrl());
            response.setStartAt(notice.getStartAt());
            response.setEndAt(notice.getEndAt());
            response.setCreatedAt(notice.getCreatedAt());

            listResponse.add(response);
        }

        return listResponse;
    }

    // 노출 공지/이벤트 유형별 목록 조회
    public List<NoticeResponseDto> getVisibleNoticesByType(String noticeType) {

        if(noticeType == null || noticeType.isBlank()) {
            throw new IllegalArgumentException("게시물 유형은 필수입니다.");
        }

        if(!noticeType.equals("NOTICE") && !noticeType.equals("EVENT")) {
            throw new IllegalArgumentException("올바르지 않은 게시물 유형입니다.");
        }

        List<Notice> notices = noticeDao.selectVisibleNoticesByType(noticeType);
        List<NoticeResponseDto> listResponse = new ArrayList<>();

        for(Notice notice : notices) {
            NoticeResponseDto response = new NoticeResponseDto();
            response.setNoticeId(notice.getNoticeId());
            response.setTitle(notice.getTitle());
            response.setContent(notice.getContent());
            response.setNoticeType(notice.getNoticeType());
            response.setImageUrl(notice.getImageUrl());
            response.setStartAt(notice.getStartAt());
            response.setEndAt(notice.getEndAt());
            response.setCreatedAt(notice.getCreatedAt());

            listResponse.add(response);
        }

        return listResponse;
    }

    // 노출 공지/이벤트 상세 조회
    public NoticeResponseDto getVisibleNoticeById(Integer noticeId) {

        if(noticeId == null) {
            throw new IllegalArgumentException("공지 ID는 필수입니다.");
        }

        Notice notice = noticeDao.selectVisibleNoticeById(noticeId);

        if(notice == null) {
            throw new IllegalArgumentException("존재하지 않는 공지/이벤트입니다.");
        }

        NoticeResponseDto response = new NoticeResponseDto();
        response.setNoticeId(notice.getNoticeId());
        response.setTitle(notice.getTitle());
        response.setContent(notice.getContent());
        response.setNoticeType(notice.getNoticeType());
        response.setImageUrl(notice.getImageUrl());
        response.setStartAt(notice.getStartAt());
        response.setEndAt(notice.getEndAt());
        response.setCreatedAt(notice.getCreatedAt());

        return response;
    }
}
