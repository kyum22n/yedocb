import java.time.LocalDateTime;

import lombok.Data;

/**
 * 파일명: Inquiry.java
 * 설명: 문의 정보 관련 엔티티
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 필드 수정
 */

@Data
public class Inquiry {
    // 문의 ID
    private Integer inquiryId;
    // 문의자(사용자) ID
    private Integer memberId;
    // 문의 유형
    private String inquiryType;
    // 문의 제목
    private String title;
    // 문의 내용
    private String content;
    // 문의 처리 상태
    private String inquiryStatus;
    // 생성일
    private LocalDateTime createdAt;
    // 수정일(추가)
    private LocalDateTime updatedAt;
}
