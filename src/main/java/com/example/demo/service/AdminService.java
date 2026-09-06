package com.example.demo.service;

import com.example.demo.exception.DuplicateResourceException;

import com.example.demo.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.demo.dao.AdminDao;
import com.example.demo.entity.Admin;
import com.example.demo.dto.request.admin.AdminCreateRequestDto;
import com.example.demo.dto.request.admin.AdminUpdateRequestDto;
import com.example.demo.dto.response.admin.AdminDetailResponseDto;
import com.example.demo.dto.response.admin.AdminListResponseDto;
import com.example.demo.dto.request.admin.AdminPasswordUpdateRequestDto;

/**
 * 파일명: AdminService.java
 * 설명: 관리자 정보 관련 service
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 로그인 메소드 분리
 */

@Service
public class AdminService {

    @Autowired
    private AdminDao adminDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 관리자 목록 조회 (비밀번호 해시가 포함되지 않는 DTO 리스트로 반환)
    public List<AdminListResponseDto> getAllAdmins() {
        return adminDao.selectAllAdmins().stream()
                .map(AdminListResponseDto::from)
                .toList();
    }

    // 관리자 상세 조회
    public AdminDetailResponseDto getAdminById(Integer adminId) {

        Admin admin = adminDao.selectAdminById(adminId);

        if(admin == null) {
            throw new ResourceNotFoundException("존재하지 않는 관리자입니다.");
        }

        return AdminDetailResponseDto.from(admin);
    }

    // 관리자 등록
    public int createAdmin(AdminCreateRequestDto request) {

        // 로그인 아이디 중복 체크
        Admin existingAdmin = adminDao.selectAdminByLoginId(request.getAdminLoginId());

        if(existingAdmin != null) {
            throw new DuplicateResourceException("이미 사용 중인 관리자 로그인 ID입니다.");
        }

        Admin admin = new Admin();
        admin.setAdminLoginId(request.getAdminLoginId());
        admin.setAdminPassword(passwordEncoder.encode(request.getAdminPassword()));
        admin.setAdminName(request.getAdminName());
        admin.setAdminEmail(request.getAdminEmail());
        admin.setAdminPhone(request.getAdminPhone());
        admin.setAdminRole(
            (request.getAdminRole() == null || request.getAdminRole().isBlank())
                ? "ADMIN"
                : request.getAdminRole()
        );

        return adminDao.insertAdmin(admin);
    }

    // 관리자 정보 수정
    public int modifyAdmin(AdminUpdateRequestDto request) {
        Admin existingAdmin = adminDao.selectAdminById(request.getAdminId());

        if(existingAdmin == null) {
            throw new ResourceNotFoundException("존재하지 않는 관리자입니다.");
        }

        return adminDao.updateAdmin(request);
    }

    // 관리자 삭제
    public int removeAdmin(Integer adminId) {
        Admin existingAdmin = adminDao.selectAdminById(adminId);

        if(existingAdmin == null) {
            throw new ResourceNotFoundException("존재하지 않는 관리자입니다.");
        }

        return adminDao.deleteAdmin(adminId);
    }

    // 로그인 ID로 관리자 조회
    public Admin getAdminByLoginId(String adminLoginId) {
        return adminDao.selectAdminByLoginId(adminLoginId);
    }


}
