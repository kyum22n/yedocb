import java.time.LocalDateTime;

import lombok.Data;

/**
 * 파일명: User.java
 * 설명: 사용자 정보 관련 엔티티
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 필드 추가
 */

@Data
public class User {
    // 사용자 ID(추가)
    private Integer uId;
    // 로그인 아이디
    private String uLoginId;
    // 로그인 패스워드
    private String uPwd;
    // 사용자 이메일
    private String uEmail;
    // 사용자 이름
    private String uName;
    // 사용자 전화번호
    private String uPhone;
    // 생성일(추가)
    private LocalDateTime createdAt;
    // 수정일(추가)
    private LocalDateTime updatedAt;
}
