package com.example.demo.dto.response.auth;

import com.example.demo.entity.Admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 파일명: AdminTokenResponseDto.java
 * 설명: 관리자 로그인 응답 DTO. 관리자 하위 API(예약/문의/상담 등)가 요청 바디에
 *       숫자 adminId를 필요로 하고, 프론트가 SUPERADMIN 전용 메뉴를 분기하려면
 *       adminRole도 필요하기 때문에 로그인 응답에 함께 내려준다.
 *       관리자 로그인은 리프레시 토큰을 발급하지 않는다(B 원본 동작 유지, TokenResponseDto와 동일 정책).
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | 최초 생성 — 프론트엔드 세션 문의(adminId/adminRole 누락) 대응
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminTokenResponseDto {
    private String accessToken;
    private Integer adminId;
    private String adminLoginId;
    private String adminRole;

    public static AdminTokenResponseDto from(String accessToken, Admin admin) {
        return new AdminTokenResponseDto(accessToken, admin.getAdminId(), admin.getAdminLoginId(), admin.getAdminRole());
    }
}
