package com.example.demo.dao;

import org.apache.ibatis.annotations.Mapper;
import com.example.demo.entity.Member;
import java.util.List;

/**
 * 파일명: MemberDao.java
 * 설명: 사용자 정보 관련 DAO 인터페이스
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 인터페이스 생성
 */

@Mapper
public interface MemberDao {
    
    // 관리자 회원 등록 / 회원가입
    public int insertMember(Member member);

    // 관리자 회원 목록 조회
    public List<Member> selectAllMembers();

    // 관리자 회원 상세 조회 / 회원 마이페이지 조회
    public Member selectMemberById(Integer memberId);

    // 로그인 ID로 회원 조회
    public Member selectMemberByLoginId(String memberLoginId);

    // 이메일로 회원 조회
    public Member selectMemberByEmail(String memberEmail);

    // 회원 마이페이지 수정
    public int updateMember(Member member);

    // 관리자 회원 삭제 / 회원 탈퇴
    public int deleteMember(Integer memberId);
}
