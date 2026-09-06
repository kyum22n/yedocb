package com.example.demo.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 파일명: UserPasswordUpdateRequestDto.java
 * 설명: 마이페이지 비밀번호 변경 요청 DTO. 대상 계정(uId)은 요청 바디가 아니라
 *       JWT 인증 주체에서 가져온다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | 신규 생성 — 프론트엔드 세션 요청 대응(마이페이지 비밀번호 변경 기능 부재)
 */
@Data
public class UserPasswordUpdateRequestDto {
    // 현재 비밀번호 (본인 확인용)
    @NotBlank
    private String currentPwd;

    // 새 비밀번호 (User 엔티티와 동일한 복잡도 규칙)
    @NotBlank
    @Size(min = 8, max = 20)
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,20}$",
        message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다."
    )
    private String newPwd;
}
