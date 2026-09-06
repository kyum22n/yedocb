package com.example.demo.dto.request.user;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 파일명: UserMypageUpdateRequestDto.java
 * 설명: 사용자 마이페이지 정보 수정 요청 DTO (구 MemberMypageUpdateRequestDto 대체)
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | Member -> User 통합 (Phase 1). java.time.LocalDate import 누락 버그 수정
 */
@Data
public class UserMypageUpdateRequestDto {
    // 회원 ID (로그인 ID, PK) - 향후 JWT 도입 시 Authentication에서 조회하고 이 필드는 제거 예정 (TODO)
    @NotBlank
    private String uId;
    // 회원 이름
    private String uName;
    // 회원 전화번호
    private String uPhone;
    // 회원 생년월일
    private LocalDate uBirth;
    // 회원 성별
    private String uGender;
}
