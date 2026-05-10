package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dao.MemberDao;
import com.example.demo.dto.request.member.MemberCreateRequestDto;
import com.example.demo.dto.request.member.MemberMypageUpdateRequestDto;
import com.example.demo.dto.response.member.MemberMypageResponseDto;
import com.example.demo.entity.Member;

/**
 * 파일명: MemberService.java
 * 설명: 회원 정보 관련 service
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 로그인 메소드 분리
 */

@Service
public class MemberService {

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 회원가입
    public int createMember(MemberCreateRequestDto request) {

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

    // 마이페이지 조회
    public MemberMypageResponseDto getMemberById(Integer memberId) {
        
        Member member = memberDao.selectMemberById(memberId);

        if(member == null) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }

        MemberMypageResponseDto response = new MemberMypageResponseDto();
        response.setMemberId(member.getMemberId());
        response.setMemberLoginId(member.getMemberLoginId());
        response.setMemberName(member.getMemberName());
        response.setMemberEmail(member.getMemberEmail());
        response.setMemberPhone(member.getMemberPhone());
        response.setMemberBirth(member.getMemberBirth());
        response.setMemberGender(member.getMemberGender());

        return response;
    }

    // 마이페이지 수정
    public int modifyMember(MemberMypageUpdateRequestDto request) {

        if(memberDao.selectMemberById(request.getMemberId()) == null) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }

        Member updatedMember = new Member();
        updatedMember.setMemberId(request.getMemberId());
        updatedMember.setMemberName(request.getMemberName());
        updatedMember.setMemberPhone(request.getMemberPhone());
        updatedMember.setMemberBirth(request.getMemberBirth());
        updatedMember.setMemberGender(request.getMemberGender());

        return memberDao.updateMember(updatedMember);
    }

    // 회원 탈퇴
    public int removeMember(Integer memberId) {

        if(memberDao.selectMemberById(memberId) == null) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }

        return memberDao.deleteMember(memberId);
    }
}
