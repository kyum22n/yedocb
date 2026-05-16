package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.entity.Inquiry;
import com.example.demo.entity.InquiryAnswer;

/**
 * 파일명: InquiryDao.java
 * 설명: 사용자용 문의 관련 DAO 인터페이스
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 인터페이스 생성
 */

@Mapper
public interface InquiryDao {

    // 문의 등록
    public int insertInquiry(Inquiry inquiry);

    // 회원별 문의 목록 조회
    public List<Inquiry> selectInquiriesByMemberId(Integer memberId);

    // 문의 상세 조회
    public Inquiry selectInquiryById(@Param("inquiryId") Integer inquiryId,
                                     @Param("memberId") Integer memberId);

    // 문의 답변 조회
    public InquiryAnswer selectAnswerByInquiryId(Integer inquiryId);

    // 문의 수정
    public int updateInquiry(Inquiry inquiry);

    // 문의 답변 삭제
    public int deleteAnswerByInquiryId(Integer inquiryId);

    // 문의 삭제
    public int deleteInquiry(@Param("inquiryId") Integer inquiryId,
                             @Param("memberId") Integer memberId);
}
