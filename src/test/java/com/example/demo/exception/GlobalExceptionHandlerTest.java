package com.example.demo.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * 파일명: GlobalExceptionHandlerTest.java
 * 설명: GlobalExceptionHandler의 각 커스텀 예외 -> HTTP 상태 코드 매핑 검증.
 *       컨트롤러 어드바이스 테스트 하네스가 아직 없으므로 핸들러 메소드를 직접 호출한다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | Phase 1 단위 테스트 작성
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void ResourceNotFoundException은_404를_반환한다() {
        ResponseEntity<ErrorResponse> response =
                handler.handleResourceNotFound(new ResourceNotFoundException("존재하지 않는 회원입니다."));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().message()).isEqualTo("존재하지 않는 회원입니다.");
    }

    @Test
    void DuplicateResourceException은_409를_반환한다() {
        ResponseEntity<ErrorResponse> response =
                handler.handleDuplicateResource(new DuplicateResourceException("이미 존재하는 아이디입니다."));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().status()).isEqualTo(409);
    }

    @Test
    void UnauthorizedActionException은_403을_반환한다() {
        ResponseEntity<ErrorResponse> response =
                handler.handleUnauthorizedAction(new UnauthorizedActionException("권한이 없습니다."));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().status()).isEqualTo(403);
    }

    @Test
    void InvalidVerificationCodeException은_400을_반환한다() {
        ResponseEntity<ErrorResponse> response =
                handler.handleInvalidVerificationCode(new InvalidVerificationCodeException("인증 코드가 올바르지 않습니다."));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().status()).isEqualTo(400);
    }

    @Test
    void InvalidCredentialsException은_401을_반환한다() {
        ResponseEntity<ErrorResponse> response =
                handler.handleInvalidCredentials(new InvalidCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다."));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().status()).isEqualTo(401);
    }

    @Test
    void 알수없는_Exception은_500을_반환한다() {
        ResponseEntity<ErrorResponse> response =
                handler.handleException(new RuntimeException("예상치 못한 오류"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().status()).isEqualTo(500);
    }
}
