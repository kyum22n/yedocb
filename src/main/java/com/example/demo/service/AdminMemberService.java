package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dao.MemberDao;
import com.example.demo.dto.request.member.MemberCreateRequestDto;
import com.example.demo.dto.response.member.AdminMemberListResponseDto;
import com.example.demo.dto.response.member.AdminMemberDetailResponseDto;
import com.example.demo.entity.Member;

/**
 * 파일명: AdminMemberService.java
 * 설명: 관리자 회원 정보 관련 service
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 클래스 생성
 */

@Service
public class AdminMemberService {

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 관리자 회원 등록
    public int createAdminMember(MemberCreateRequestDto request) {

        // 로그인 ID 중복 체크
        if(memberDao.selectMemberByLoginId(request.getMemberLoginId()) != null) {
            throw new IllegalArgumentException("이미 존재하는 로그인 ID입니다.");
        }

        /// 이메일 중복 체크
        if(memberDao.selectMemberByEmail(request.getMemberEmail()) != null) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        Member newMember = new Member();
        newMember.setMemberLoginId(request.getMemberLoginId());
        newMember.setMemberPassword(passwordEncoder.encode(request.getMemberPassword()));
        newMember.setMemberName(request.getMemberName());
        newMember.setMemberEmail(request.getMemberEmail());
        newMember.setMemberPhone(request.getMemberPhone());
        newMember.setMemberBirth(request.getMemberBirth());
        newMember.setMemberGender(request.getMemberGender());

        return memberDao.insertMember(newMember);
    }

    // 관리자 회원 목록 조회
    public List<Member> getAllMembers() {

        return memberDao.selectAllMembers();
    }

    // 관리자 회원 상세 조회
    public AdminMemberDetailResponseDto getAdminMemberById(Integer memberId) {

        Member member = memberDao.selectMemberById(memberId);

        if(member == null) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }

        AdminMemberDetailResponseDto response = new AdminMemberDetailResponseDto();
        response.setMemberId(member.getMemberId());
        response.setMemberLoginId(member.getMemberLoginId());
        response.setMemberName(member.getMemberName());
        response.setMemberEmail(member.getMemberEmail());
        response.setMemberPhone(member.getMemberPhone());
        response.setMemberBirth(member.getMemberBirth());
        response.setMemberGender(member.getMemberGender()); 
        response.setCreatedAt(member.getCreatedAt());
        response.setUpdatedAt(member.getUpdatedAt());

        return response;
    }

    // 관리자 회원 삭제
    public int removeAdminMember(Integer memberId) {

        if(memberDao.selectMemberById(memberId) == null) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }

        return memberDao.deleteMember(memberId);
    }
}
