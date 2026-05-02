import java.time.LocalDateTime;

import lombok.Data;

/**
 * 파일명: Reservation.java
 * 설명: 예약 정보 관련 엔티티
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 필드 추가
 */

@Data
public class Reservation {
    // 예약ID
    private Integer rId;
    // 예약자 ID
    private String uId;
    // 상담 항목
    private String tName;
    // 상담일자
    private Date consultDate;
    // 상담시간
    private LocalDateTime consultTime;
    // 예약 상태
    private String status;
    // 생성일(추가)
    private LocalDateTime createdAt;
    // 수정일(추가)
    private LocalDateTime updatedAt;
}
