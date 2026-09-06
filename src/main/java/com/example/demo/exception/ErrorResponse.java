package com.example.demo.exception;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 파일명: ErrorResponse.java
 * 설명: 예외 처리 시 반환되는 공통 에러 응답 형태.
 *       (일반 엔드포인트의 성공 응답은 ApiResponse&lt;T&gt; 같은 공통 래퍼를 사용하지 않고
 *        ResponseEntity&lt;ExactDtoType&gt;을 그대로 반환하는 프로젝트 컨벤션을 따른다.
 *        이 ErrorResponse는 예외 상황에 한정된 별도의 응답 형태이다.)
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | 전역 예외 처리 도입 (Phase 1)
 */
public record ErrorResponse(
        int status,
        String message,
        LocalDateTime timestamp,
        List<FieldError> fieldErrors
) {
    public ErrorResponse(int status, String message, LocalDateTime timestamp) {
        this(status, message, timestamp, null);
    }

    public record FieldError(String field, String reason) {
    }
}
