package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.reservation.ReservationCancelRequestDto;
import com.example.demo.dto.request.reservation.ReservationCreateRequestDto;
import com.example.demo.dto.request.reservation.ReservationUpdateRequestDto;
import com.example.demo.dto.response.reservation.ReservationResponseDto;
import com.example.demo.service.ReservationService;

/**
 * 파일명: ReservationController.java
 * 설명: 사용자용 예약 관련 controller
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-05-16 | 규민 | 클래스 생성
 */

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    // 예약 등록
    @PostMapping("/register")
    public ResponseEntity<Integer> registerReservation(@RequestBody ReservationCreateRequestDto request) {
        return ResponseEntity.ok(reservationService.createReservation(request));
    }

    // 회원별 예약 목록 조회
    @GetMapping("/member")
    public ResponseEntity<List<ReservationResponseDto>> getReservationsByMemberId(
            @RequestParam("memberId") Integer memberId) {
        return ResponseEntity.ok(reservationService.getReservationsByMemberId(memberId));
    }

    // 예약 상세 조회
    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationResponseDto> getReservationDetail(
            @PathVariable("reservationId") Integer reservationId,
            @RequestParam("memberId") Integer memberId) {
        return ResponseEntity.ok(reservationService.getReservationById(reservationId, memberId));
    }

    // 예약 정보 수정
    @PutMapping("/update")
    public ResponseEntity<Integer> updateReservation(@RequestBody ReservationUpdateRequestDto request) {
        return ResponseEntity.ok(reservationService.modifyReservation(request));
    }

    // 예약 취소
    @PutMapping("/cancel")
    public ResponseEntity<Integer> cancelReservation(@RequestBody ReservationCancelRequestDto request) {
        return ResponseEntity.ok(reservationService.cancelReservation(request));
    }
}
