package com.example.demo.dto.response.notice;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 파일명: NoticeResponseDto.java
 * 설명: 사용자 공지/이벤트 응답 DTO
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-25 | 규민 | 클래스 생성
 */

@Data
public class NoticeResponseDto {
    // 공지 ID
    private Integer noticeId;
    // 제목
    private String title;
    // 내용
    private String content;
    // 게시물 유형
    private String noticeType;
    // 이미지 URL
    private String imageUrl;
    // 이벤트 시작일
    private LocalDateTime startAt;
    // 이벤트 종료일
    private LocalDateTime endAt;
    // 생성일
    private LocalDateTime createdAt;
}
