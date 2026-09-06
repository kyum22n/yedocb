package com.example.demo.dto.request.user;

import java.time.LocalDate;

import lombok.Data;

/**
 * 파일명: UserMypageUpdateRequestDto.java
 * 설명: 사용자 마이페이지 정보 수정 요청 DTO (구 MemberMypageUpdateRequestDto 대체).
 *       수정 대상(uId)은 요청 바디가 아니라 JWT 인증 주체에서 가져온다 — 요청 바디의
 *       임의 uId를 신뢰해 다른 사용자 정보를 수정할 수 있던 문제를 막기 위함.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | Member -> User 통합 (Phase 1). java.time.LocalDate import 누락 버그 수정
 * 2026-09-06 | 리팩토링 | uId 필드 제거, 인증 주체 기반으로 변경 (알려진 이슈 정리)
 */
@Data
public class UserMypageUpdateRequestDto {
    // 회원 이름
    private String uName;
    // 회원 전화번호
    private String uPhone;
    // 회원 생년월일
    private LocalDate uBirth;
    // 회원 성별
    private String uGender;
}
