package com.example.demo.exception;

/**
 * 파일명: ResourceNotFoundException.java
 * 설명: 요청한 리소스가 존재하지 않을 때 발생하는 예외 (HTTP 404)
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | 전역 예외 처리 도입 (Phase 1)
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
