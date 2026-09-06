package com.example.demo.dto.request.user;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 파일명: UserCreateRequestDto.java
 * 설명: 사용자 회원가입 요청 DTO (구 MemberCreateRequestDto 대체)
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | Member -> User 통합 (Phase 1). java.time.LocalDate import 누락 버그 수정,
 *                        Bean Validation 제약 추가 (User 엔티티와 동일한 제약)
 */
@Data
public class UserCreateRequestDto {
    // 로그인 아이디
    @NotBlank
    @Size(min = 4, max = 20)
    private String uId;

    // 로그인 패스워드
    @NotBlank
    @Size(min = 8, max = 20)
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,20}$",
        message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다."
    )
    private String uPwd;

    // 이름
    @NotBlank
    private String uName;

    // 이메일
    @NotBlank
    @Email
    private String uEmail;

    // 전화번호
    private String uPhone;

    // 생년월일
    private LocalDate uBirth;

    // 성별
    private String uGender;
}
