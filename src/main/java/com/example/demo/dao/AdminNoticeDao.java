package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.entity.Notice;

/**
 * 파일명: AdminNoticeDao.java
 * 설명: 관리자용 공지/이벤트 관련 DAO 인터페이스
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-25 | 규민 | 인터페이스 생성
 */

@Mapper
public interface AdminNoticeDao {

    // 공지/이벤트 등록
    public int insertNotice(Notice notice);

    // 공지/이벤트 목록 조회
    public List<Notice> selectAllNotices();

    // 공지/이벤트 유형별 목록 조회
    public List<Notice> selectNoticesByType(String noticeType);

    // 공지/이벤트 상세 조회
    public Notice selectNoticeById(Integer noticeId);

    // 공지/이벤트 수정
    public int updateNotice(Notice notice);

    // 공지/이벤트 삭제
    public int deleteNotice(Integer noticeId);
}
