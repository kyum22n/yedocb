package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 파일명: AdminMemberController.java
 * 설명: 관리자 회원 정보 관련 controller
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 클래스 생성
 */

@RestController
@RequestMapping("/admin/member")
public class AdminMemberController {
    
    @Autowired
    private AdminMemberService adminMemberService;

    // 관리자 회원 등록
    @PostMapping("/register")
    public int registerAdminMember(@RequestBody MemberCreateRequestDto request) {
        return adminMemberService.createAdminMember(request);
    }

    // 관리자 회원 목록 조회
    @GetMapping("/list")
    public List<AdminMemberListResponseDto> getAllMembers() {
        return adminMemberService.getAllMembers();
    }

    // 관리자 회원 상세 조회
    @GetMapping("/detail")
    public AdminMemberDetailResponseDto getMemberDetail(@RequestParam("memberId") Integer memberId) {
        return adminMemberService.getMemberById(memberId);
    }

    // 관리자 회원 삭제
    @DeleteMapping("/delete")
    public int deleteMember(@RequestParam("memberId") Integer memberId) {
        return adminMemberService.removeMember(memberId);
    }
}
