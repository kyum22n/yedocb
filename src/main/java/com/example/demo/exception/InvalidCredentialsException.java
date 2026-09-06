package com.example.demo.exception;

/**
 * 파일명: InvalidCredentialsException.java
 * 설명: 로그인 자격 증명이 올바르지 않을 때 발생하는 예외 (HTTP 401)
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | 전역 예외 처리 도입 (Phase 1)
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
