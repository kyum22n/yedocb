package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dao.UserDao;
import com.example.demo.dto.request.user.UserCreateRequestDto;
import com.example.demo.dto.response.user.AdminUserDetailResponseDto;
import com.example.demo.dto.response.user.AdminUserListResponseDto;
import com.example.demo.entity.User;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.ResourceNotFoundException;

/**
 * 파일명: AdminUserService.java
 * 설명: 관리자 회원 정보 관련 서비스 클래스 (구 AdminMemberService 대체)
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | Member -> User 통합 (Phase 1). IllegalArgumentException을
 *                        커스텀 예외로 교체, getAllUsers()가 엔티티가 아닌 DTO 리스트를
 *                        반환하도록 수정 (비밀번호 해시 노출 버그 수정)
 */
@Service
public class AdminUserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 관리자 회원 등록
    public int createAdminUser(UserCreateRequestDto request) {

        // 로그인 ID 중복 체크
        if (userDao.selectUserById(request.getUId()) != null) {
            throw new DuplicateResourceException("이미 존재하는 아이디입니다.");
        }

        // 이메일 중복 체크
        if (userDao.selectUserByEmail(request.getUEmail()) != null) {
            throw new DuplicateResourceException("이미 존재하는 이메일입니다.");
        }

        User newUser = new User();
        newUser.setUId(request.getUId());
        newUser.setUPwd(passwordEncoder.encode(request.getUPwd()));
        newUser.setUName(request.getUName());
        newUser.setUEmail(request.getUEmail());
        newUser.setUPhone(request.getUPhone());
        newUser.setUBirth(request.getUBirth());
        newUser.setUGender(request.getUGender());

        return userDao.insertUser(newUser);
    }

    // 관리자 회원 목록 조회 (비밀번호 해시가 노출되지 않도록 DTO 리스트로 반환)
    public List<AdminUserListResponseDto> getAllUsers() {

        return userDao.selectAllUsers().stream()
                .map(AdminUserListResponseDto::from)
                .toList();
    }

    // 관리자 회원 상세 조회
    public AdminUserDetailResponseDto getAdminUserById(String uId) {

        User user = userDao.selectUserById(uId);

        if (user == null) {
            throw new ResourceNotFoundException("존재하지 않는 회원입니다.");
        }

        return AdminUserDetailResponseDto.from(user);
    }

    // 관리자 회원 삭제
    public int removeAdminUser(String uId) {

        if (userDao.selectUserById(uId) == null) {
            throw new ResourceNotFoundException("존재하지 않는 회원입니다.");
        }

        return userDao.deleteUser(uId);
    }
}
