package com.example.demo.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 파일명: User.java
 * 설명: 회원 정보 엔티티 (B의 User 구조 기반 + 생년월일/성별 필드 추가). 테이블명 users.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | Member -> User 통합 (Phase 1)
 */

@Data
public class User {
    // 로그인 ID (PK)
    @NotBlank
    @Size(min = 4, max = 20)
    private String uId;

    // 로그인 패스워드 (해시 저장)
    @NotBlank
    @Size(min = 8, max = 20)
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,20}$",
        message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다."
    )
    private String uPwd;

    // 사용자 이메일
    @NotBlank
    @Email
    private String uEmail;

    // 사용자 이름
    @NotBlank
    private String uName;

    // 사용자 전화번호
    private String uPhone;

    // 사용자 생년월일
    private LocalDate uBirth;

    // 사용자 성별
    private String uGender;

    // 생성일
    private LocalDateTime createdAt;

    // 수정일
    private LocalDateTime updatedAt;
}
