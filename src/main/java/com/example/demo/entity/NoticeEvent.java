import java.time.LocalDateTime;

import lombok.Data;

/**
 * 파일명: NoticeEvent.java
 * 설명: 공지/이벤트 정보 관련 엔티티
*
* ===============================
* 수정 이력
* ===============================
* 2026-05-02 | 규민 | 필드 추가
*/

@Data
public class NoticeEvent {
    // 공지 ID
    private Integer nId;
    // 공지 제목
    private String nTitle;
    // 공지 내용
    private String nContent;
    // 공지 관련 이미지 URL
    private String nImageUrl;
    // 게시물 유형(공지/이벤트)
    private String nType;

    // (이벤트일 경우)
    // 이벤트 시작일
    private LocalDateTime nStartDate;
    // 이벤트 종료일
    private LocalDateTime nEndDate;

    // 생성일
    private LocalDateTime createdAt;
    // 수정일
    private LocalDateTime updatedAt;
}
