package com.example.demo.exception;

/**
 * 파일명: InvalidVerificationCodeException.java
 * 설명: 인증 코드가 올바르지 않을 때 발생하는 예외 (HTTP 400)
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | 전역 예외 처리 도입 (Phase 1)
 */
public class InvalidVerificationCodeException extends RuntimeException {

    public InvalidVerificationCodeException(String message) {
        super(message);
    }
}
