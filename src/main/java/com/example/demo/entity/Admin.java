import java.time.LocalDateTime;

import lombok.Data;

/**
 * 파일명: Admin.java
 * 설명: 관리자 정보 관련 엔티티
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 필드 추가
 */

@Data
public class Admin {
    // 관리자 ID(추가)
    private Integer aId;
    // 관리자 로그인 ID
    private String aLoginId;
    // 관리자 비밀번호
    private String aPwd;
    // 관리자 이메일
    private String aEmail;
    // 관리자 전화번호(추가)
    private String aPhone;
    // 관리자 권한 - 최고관리자/일반관리자
    private String aRole;
    // 관리자 계정 생성자
    private String createdBy;
    // 생성일(추가)
    private LocalDateTime createdAt;
    // 수정일(추가)
    private LocalDateTime updatedAt;
}
