import java.time.LocalDateTime;

import lombok.Data;

/**
 * 파일명: Treatment.java
 * 설명: 시술/수술 정보 관련 엔티티
*
* ===============================
* 수정 이력
* ===============================
* 2026-05-02 | 규민 | 엔티티 추가
*/

@Data
public class Treatment {
    // 항목 ID
    private Integer tId;
    // 항목명
    private String tName;
    // 생성일
    private LocalDateTime createdAt;
    // 수정일
    private LocalDateTime updatedAt;
}
