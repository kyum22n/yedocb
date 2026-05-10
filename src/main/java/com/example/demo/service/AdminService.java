package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.demo.dao.AdminDao;
import com.example.demo.entity.Admin;
import com.example.demo.dto.request.admin.AdminUpdateRequestDto;
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

    // 관리자 목록 조회
    public List<Admin> getAllAdmins() {
        return adminDao.selectAllAdmins();
    }

    // 관리자 상세 조회
    public Admin getAdminById(Integer adminId) {
        return adminDao.selectAdminById(adminId);
    }

    // 관리자 등록
    public int createAdmin(Admin admin) {
        
        // 로그인 아이디 중복 체크
        Admin existingAdmin = adminDao.selectAdminByLoginId(admin.getAdminLoginId());
        
        if(existingAdmin != null) {
            throw new IllegalArgumentException("이미 사용 중인 관리자 로그인 ID입니다.");
        }

        // 패스워드 암호화
        String encodePassword = passwordEncoder.encode(admin.getAdminPassword());
        admin.setAdminPassword(encodePassword);

        if(admin.getAdminRole() == null || admin.getAdminRole().isBlank()) {
            admin.setAdminRole("ADMIN");
        }
 
        return adminDao.insertAdmin(admin);
    }

    // 관리자 정보 수정
    public int modifyAdmin(AdminUpdateRequestDto admin) {
        Admin existingAdmin = adminDao.selectAdminById(admin.getAdminId());

        if(existingAdmin == null) {
            throw new IllegalArgumentException("존재하지 않는 관리자입니다.");
        }

        return adminDao.updateAdmin(admin);
    }

    // 관리자 삭제
    public int removeAdmin(Integer adminId) {
        Admin existingAdmin = adminDao.selectAdminById(adminId);

        if(existingAdmin == null) {
            throw new IllegalArgumentException("존재하지 않는 관리자입니다.");
        }

        return adminDao.deleteAdmin(adminId);
    }

    // 로그인 ID로 관리자 조회
    public Admin getAdminByLoginId(String adminLoginId) {
        return adminDao.selectAdminByLoginId(adminLoginId);
    }


}
