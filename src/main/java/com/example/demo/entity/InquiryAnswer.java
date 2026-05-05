package com.example.demo.entity;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * 파일명: InquiryAnswer.java
 * 설명: 문의답변 정보 관련 엔티티
*
* ===============================
* 수정 이력
* ===============================
* 2026-05-02 | 규민 | 엔티티 추가
*/

@Data
public class InquiryAnswer {
    // 답변 ID
    private Integer answerId;
    // 문의 ID
    private Integer inquiryId;
    // 답변자(관리자) ID
    private Integer adminId;
    // 답변 내용
    private String answerContent;
    // 생성일
    private LocalDateTime createdAt;
    // 수정일
    private LocalDateTime updatedAt;

}

