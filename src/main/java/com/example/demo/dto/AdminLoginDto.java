import lombok.Data;

/**
 * 파일명: AdminLoginDto.java
 * 설명: 관리자 로그인 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 로그인 메소드 분리
 */

@Data
public class AdminLoginDto {
    private String aLoginId;
    private String aPassword;
}
