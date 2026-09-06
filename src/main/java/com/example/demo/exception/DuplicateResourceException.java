package com.example.demo.exception;

/**
 * 파일명: DuplicateResourceException.java
 * 설명: 이미 존재하는 리소스를 다시 생성하려 할 때 발생하는 예외 (HTTP 409)
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | 전역 예외 처리 도입 (Phase 1)
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
