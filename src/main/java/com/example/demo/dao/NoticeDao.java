package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.entity.Notice;

/**
 * 파일명: NoticeDao.java
 * 설명: 사용자용 공지/이벤트 관련 DAO 인터페이스
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-25 | 규민 | 인터페이스 생성
 */

@Mapper
public interface NoticeDao {

    // 노출 공지/이벤트 목록 조회
    public List<Notice> selectVisibleNotices();

    // 노출 공지/이벤트 유형별 목록 조회
    public List<Notice> selectVisibleNoticesByType(String noticeType);

    // 노출 공지/이벤트 상세 조회
    public Notice selectVisibleNoticeById(Integer noticeId);
}
