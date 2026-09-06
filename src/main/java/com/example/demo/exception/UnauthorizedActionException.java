package com.example.demo.exception;

/**
 * 파일명: UnauthorizedActionException.java
 * 설명: 권한이 없는 작업을 시도할 때 발생하는 예외 (HTTP 403)
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | 전역 예외 처리 도입 (Phase 1)
 */
public class UnauthorizedActionException extends RuntimeException {

    public UnauthorizedActionException(String message) {
        super(message);
    }
}
