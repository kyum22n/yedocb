package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.dto.request.member.MemberMypageUpdateRequestDto;
import com.example.demo.dto.response.member.MemberMypageResponseDto;

import com.example.demo.dto.request.member.MemberCreateRequestDto;
import com.example.demo.service.MemberService;

/**
 * 파일명: MemberController.java
 * 설명: 회원 정보 관련 controller
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 클래스 생성
 */

@RestController
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<Integer> registerMember(@RequestBody MemberCreateRequestDto request) {
        return ResponseEntity.ok(memberService.createMember(request));
    }

    // 마이페이지 조회
    @GetMapping("/mypage")
    public ResponseEntity<MemberMypageResponseDto> getMyPage(@RequestParam("memberId") Integer memberId) {
        return ResponseEntity.ok(memberService.getMemberById(memberId));
    }

    // 마이페이지 수정
    @PutMapping("/mypage/update")
    public ResponseEntity<Integer> updateMyPage(@RequestBody MemberMypageUpdateRequestDto request) {
        return ResponseEntity.ok(memberService.modifyMember(request));
    }

    // 회원 탈퇴
    @DeleteMapping("/withdraw")
    public ResponseEntity<Integer> withdrawMember(@RequestParam("memberId") Integer memberId) {
        return ResponseEntity.ok(memberService.removeMember(memberId));
    }

}
