package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.entity.Inquiry;
import com.example.demo.entity.InquiryAnswer;

/**
 * 파일명: AdminInquiryDao.java
 * 설명: 관리자용 문의 관련 DAO 인터페이스
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 인터페이스 생성
 */

@Mapper
public interface AdminInquiryDao {

    // 문의 목록 조회
    public List<Inquiry> selectAllInquiries();

    // 회원별 문의 목록 조회
    public List<Inquiry> selectInquiriesByMemberId(Integer memberId);

    // 문의 유형별 목록 조회
    public List<Inquiry> selectInquiriesByType(String inquiryType);

    // 문의 상태별 목록 조회
    public List<Inquiry> selectInquiriesByStatus(String inquiryStatus);

    // 문의 상세 조회
    public Inquiry selectInquiryById(Integer inquiryId);

    // 문의 상태 수정
    public int updateInquiryStatus(Inquiry inquiry);

    // 문의 삭제
    public int deleteInquiry(Integer inquiryId);

    // 답변 등록
    public int insertInquiryAnswer(InquiryAnswer inquiryAnswer);

    // 문의 답변 조회
    public InquiryAnswer selectAnswerByInquiryId(Integer inquiryId);

    // 답변 상세 조회
    public InquiryAnswer selectAnswerById(Integer answerId);

    // 답변 수정
    public int updateInquiryAnswer(InquiryAnswer inquiryAnswer);

    // 답변 삭제
    public int deleteInquiryAnswer(Integer answerId);

    // 문의 답변 삭제
    public int deleteAnswerByInquiryId(Integer inquiryId);
}
