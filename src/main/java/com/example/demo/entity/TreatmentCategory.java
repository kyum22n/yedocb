import lombok.Data;

/**
 * 파일명: TreatmentCategory.java
 * 설명: 진료항목 카테고리 관련 엔티티
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-02 | 규민 | 엔티티 추가
 */

@Data
public class TreatmentCategory {
    // 카테고리 ID
    private Integer categoryId;
    // 카테고리 이름
    private String categoryName;
    // 노출여부
    private Boolean isVisible;
    // 생성일
    private LocalDateTime createdAt;
    // 수정일
    private LocalDateTime updatedAt;
}
