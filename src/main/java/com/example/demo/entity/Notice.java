package com.example.demo.entity;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * 파일명: Notice.java
 * 설명: 공지/이벤트 정보 관련 엔티티
*
* ===============================
* 수정 이력
* ===============================
* 2026-05-02 | 규민 | 필드 수정
*/

@Data
public class Notice {
    // 공지 ID
    private Integer noticeId;
    // 공지 제목
    private String title;
    // 공지 내용
    private String content;
    // 게시물 유형(공지/이벤트)
    private String noticeType;
    // 공지 관련 이미지 URL
    private String imageUrl;
    // 공지 노출 여부
    private Boolean isVisible;

    // (이벤트일 경우)
    // 이벤트 시작일
    private LocalDateTime startAt;
    // 이벤트 종료일
    private LocalDateTime endAt;

    // 공지 작성자(관리자) ID
    private Integer createdBy;

    // 생성일
    private LocalDateTime createdAt;
    // 수정일
    private LocalDateTime updatedAt;
}
