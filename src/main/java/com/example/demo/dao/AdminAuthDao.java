package com.example.demo.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 파일명: AdminAuthDao.java
 * 설명: 관리자 인증 관련 DAO 인터페이스
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 인터페이스 생성
 */

@Mapper
public interface AdminAuthDao {
    String selectAdminIdByEmail(String adminEmail);

    int updatePassword(@Param("adminId") Integer adminId,
                       @Param("adminPassword") String adminPassword);
}
