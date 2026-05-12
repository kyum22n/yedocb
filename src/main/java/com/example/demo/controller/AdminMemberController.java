package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.util.List;

import com.example.demo.entity.Member;
import com.example.demo.service.AdminMemberService;

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
    public ResponseEntity<Integer> registerAdminMember(@RequestBody MemberCreateRequestDto request) {
        return ResponseEntity.ok(adminMemberService.createAdminMember(request));
    }

    // 관리자 회원 목록 조회
    @GetMapping("/list")
    public ResponseEntity<List<Member>> getAllMembers() {
        return ResponseEntity.ok(adminMemberService.getAllMembers());
    }

    // 관리자 회원 상세 조회
    @GetMapping("/detail")
    public ResponseEntity<AdminMemberDetailResponseDto> getMemberDetail(@RequestParam("memberId") Integer memberId) {
        return ResponseEntity.ok(adminMemberService.getAdminMemberById(memberId));
    }

    // 관리자 회원 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<Integer> deleteMember(@RequestParam("memberId") Integer memberId) {
        return ResponseEntity.ok(adminMemberService.removeAdminMember(memberId));
    }
}
