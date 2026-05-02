import java.time.LocalDateTime;

import lombok.Data;

/**
 * 파일명: Inquiry.java
 * 설명: 문의 정보 관련 엔티티
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 필드 추가 & 삭제
 */

@Data
public class Inquiry {
    // 문의 ID
    private Integer qId;
    // 문의자(사용자) ID
    private Integer uId;
    // 문의자 방문 여부
    private Boolean visit;
    // 문의 내용
    private String qContent;
    // 문의 상태
    private String qStatus;
    // 생성일
    private LocalDateTime createdAt;
    // 수정일(추가)
    private LocalDateTime updatedAt;
}
