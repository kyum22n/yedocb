package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.entity.User;

/**
 * 파일명: UserDao.java
 * 설명: 사용자 정보 관련 DAO 인터페이스 (구 MemberDao 대체)
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | Member -> User 통합 (Phase 1)
 */

@Mapper
public interface UserDao {

    // 회원 등록 / 회원가입
    public int insertUser(User user);

    // 회원 목록 조회
    public List<User> selectAllUsers();

    // 회원 상세 조회 / 마이페이지 조회 (uId = 로그인 ID = PK)
    public User selectUserById(String uId);

    // 이메일로 회원 조회
    public User selectUserByEmail(String uEmail);

    // 회원 정보 수정
    public int updateUser(User user);

    // 비밀번호 변경
    public int updatePassword(@Param("uId") String uId, @Param("uPwd") String uPwd);

    // 회원 삭제 / 탈퇴
    public int deleteUser(String uId);
}
