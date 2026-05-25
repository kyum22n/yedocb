package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.AdminNoticeDao;
import com.example.demo.dto.request.notice.AdminNoticeCreateRequestDto;
import com.example.demo.dto.request.notice.AdminNoticeUpdateRequestDto;
import com.example.demo.dto.response.notice.AdminNoticeResponseDto;
import com.example.demo.entity.Notice;

/**
 * 파일명: AdminNoticeService.java
 * 설명: 관리자용 공지/이벤트 관련 서비스 클래스
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-25 | 규민 | 클래스 생성
 */

@Service
public class AdminNoticeService {

    @Autowired
    private AdminNoticeDao noticeDao;

    // 공지/이벤트 등록
    public int createNotice(AdminNoticeCreateRequestDto request) {

        if(request == null) {
            throw new IllegalArgumentException("공지/이벤트 등록 요청 정보가 없습니다.");
        }

        if(request.getTitle() == null || request.getTitle().isBlank()) {
            throw new IllegalArgumentException("제목은 필수입니다.");
        }

        if(request.getContent() == null || request.getContent().isBlank()) {
            throw new IllegalArgumentException("내용은 필수입니다.");
        }

        if(request.getNoticeType() == null || request.getNoticeType().isBlank()) {
            throw new IllegalArgumentException("게시물 유형은 필수입니다.");
        }

        if(!request.getNoticeType().equals("NOTICE") && !request.getNoticeType().equals("EVENT")) {
            throw new IllegalArgumentException("올바르지 않은 게시물 유형입니다.");
        }

        if(request.getStartAt() != null && request.getEndAt() != null && request.getEndAt().isBefore(request.getStartAt())) {
            throw new IllegalArgumentException("이벤트 종료일은 시작일보다 빠를 수 없습니다.");
        }

        Notice notice = new Notice();
        notice.setTitle(request.getTitle());
        notice.setContent(request.getContent());
        notice.setNoticeType(request.getNoticeType());
        notice.setImageUrl(request.getImageUrl());
        notice.setIsVisible(request.getIsVisible());
        notice.setStartAt(request.getStartAt());
        notice.setEndAt(request.getEndAt());
        notice.setCreatedBy(request.getCreatedBy());

        return noticeDao.insertNotice(notice);
    }

    // 공지/이벤트 목록 조회
    public List<AdminNoticeResponseDto> getAllNotices() {

        List<Notice> notices = noticeDao.selectAllNotices();
        List<AdminNoticeResponseDto> listResponse = new ArrayList<>();

        for(Notice notice : notices) {
            AdminNoticeResponseDto response = new AdminNoticeResponseDto();
            response.setNoticeId(notice.getNoticeId());
            response.setTitle(notice.getTitle());
            response.setContent(notice.getContent());
            response.setNoticeType(notice.getNoticeType());
            response.setImageUrl(notice.getImageUrl());
            response.setIsVisible(notice.getIsVisible());
            response.setStartAt(notice.getStartAt());
            response.setEndAt(notice.getEndAt());
            response.setCreatedBy(notice.getCreatedBy());
            response.setCreatedAt(notice.getCreatedAt());
            response.setUpdatedAt(notice.getUpdatedAt());

            listResponse.add(response);
        }

        return listResponse;
    }

    // 공지/이벤트 유형별 목록 조회
    public List<AdminNoticeResponseDto> getNoticesByType(String noticeType) {

        if(noticeType == null || noticeType.isBlank()) {
            throw new IllegalArgumentException("게시물 유형은 필수입니다.");
        }

        if(!noticeType.equals("NOTICE") && !noticeType.equals("EVENT")) {
            throw new IllegalArgumentException("올바르지 않은 게시물 유형입니다.");
        }

        List<Notice> notices = noticeDao.selectNoticesByType(noticeType);
        List<AdminNoticeResponseDto> listResponse = new ArrayList<>();

        for(Notice notice : notices) {
            AdminNoticeResponseDto response = new AdminNoticeResponseDto();
            response.setNoticeId(notice.getNoticeId());
            response.setTitle(notice.getTitle());
            response.setContent(notice.getContent());
            response.setNoticeType(notice.getNoticeType());
            response.setImageUrl(notice.getImageUrl());
            response.setIsVisible(notice.getIsVisible());
            response.setStartAt(notice.getStartAt());
            response.setEndAt(notice.getEndAt());
            response.setCreatedBy(notice.getCreatedBy());
            response.setCreatedAt(notice.getCreatedAt());
            response.setUpdatedAt(notice.getUpdatedAt());

            listResponse.add(response);
        }

        return listResponse;
    }

    // 공지/이벤트 상세 조회
    public AdminNoticeResponseDto getNoticeById(Integer noticeId) {

        if(noticeId == null) {
            throw new IllegalArgumentException("공지 ID는 필수입니다.");
        }

        Notice notice = noticeDao.selectNoticeById(noticeId);

        if(notice == null) {
            throw new IllegalArgumentException("존재하지 않는 공지/이벤트입니다.");
        }

        AdminNoticeResponseDto response = new AdminNoticeResponseDto();
        response.setNoticeId(notice.getNoticeId());
        response.setTitle(notice.getTitle());
        response.setContent(notice.getContent());
        response.setNoticeType(notice.getNoticeType());
        response.setImageUrl(notice.getImageUrl());
        response.setIsVisible(notice.getIsVisible());
        response.setStartAt(notice.getStartAt());
        response.setEndAt(notice.getEndAt());
        response.setCreatedBy(notice.getCreatedBy());
        response.setCreatedAt(notice.getCreatedAt());
        response.setUpdatedAt(notice.getUpdatedAt());

        return response;
    }

    // 공지/이벤트 수정
    public int modifyNotice(AdminNoticeUpdateRequestDto request) {

        if(request == null) {
            throw new IllegalArgumentException("공지/이벤트 수정 요청 정보가 없습니다.");
        }

        if(request.getNoticeId() == null) {
            throw new IllegalArgumentException("공지 ID는 필수입니다.");
        }

        if(request.getTitle() == null || request.getTitle().isBlank()) {
            throw new IllegalArgumentException("제목은 필수입니다.");
        }

        if(request.getContent() == null || request.getContent().isBlank()) {
            throw new IllegalArgumentException("내용은 필수입니다.");
        }

        if(request.getNoticeType() == null || request.getNoticeType().isBlank()) {
            throw new IllegalArgumentException("게시물 유형은 필수입니다.");
        }

        if(!request.getNoticeType().equals("NOTICE") && !request.getNoticeType().equals("EVENT")) {
            throw new IllegalArgumentException("올바르지 않은 게시물 유형입니다.");
        }

        if(request.getStartAt() != null && request.getEndAt() != null && request.getEndAt().isBefore(request.getStartAt())) {
            throw new IllegalArgumentException("이벤트 종료일은 시작일보다 빠를 수 없습니다.");
        }

        Notice existingNotice = noticeDao.selectNoticeById(request.getNoticeId());

        if(existingNotice == null) {
            throw new IllegalArgumentException("존재하지 않는 공지/이벤트입니다.");
        }

        Notice notice = new Notice();
        notice.setNoticeId(request.getNoticeId());
        notice.setTitle(request.getTitle());
        notice.setContent(request.getContent());
        notice.setNoticeType(request.getNoticeType());
        notice.setImageUrl(request.getImageUrl());
        notice.setIsVisible(request.getIsVisible());
        notice.setStartAt(request.getStartAt());
        notice.setEndAt(request.getEndAt());

        return noticeDao.updateNotice(notice);
    }

    // 공지/이벤트 삭제
    public int removeNotice(Integer noticeId) {

        if(noticeId == null) {
            throw new IllegalArgumentException("공지 ID는 필수입니다.");
        }

        Notice existingNotice = noticeDao.selectNoticeById(noticeId);

        if(existingNotice == null) {
            throw new IllegalArgumentException("존재하지 않는 공지/이벤트입니다.");
        }

        return noticeDao.deleteNotice(noticeId);
    }
}
