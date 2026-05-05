package com.example.demo.entity;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * 파일명: Review.java
 * 설명: 후기 정보 관련 엔티티
*
* ===============================
* 수정 이력
* ===============================
* 2026-05-02 | 규민 | 필드 추가
*/

@Data
public class Review {
    // 후기 게시물 ID
    private Integer bId;
    // 시술/수술 항목 ID
    private Integer tId;
    // 작성자(사용자) ID
    private Integer uId;
    // 게시물 제목
    private String bTitle;
    // 게시물 내용
    private String bContent;
    // 게시물 이미지 URL
    private String bImageUrl;
    // 게시물 해시태그
    private String bHashTag;
    // 조회수
    private int bHits;
    // 생성일(추가)
    private LocalDateTime createdAt;
    // 수정일(추가)
    private LocalDateTime updatedAt;
}
