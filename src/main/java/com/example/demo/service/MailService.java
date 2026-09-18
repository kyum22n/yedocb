package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * 파일명: MailService.java
 * 설명: 회원 대상 안내 메일 발송 서비스 (아이디/비밀번호 찾기 전용). SMTP 설정은
 *       application*.properties의 spring.mail.* (MAIL_HOST/MAIL_USERNAME/MAIL_PASSWORD
 *       환경변수)를 통해 주입되며, Spring Boot가 JavaMailSender 빈을 자동 구성한다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-18 | 디버깅 | 신규 생성 — 아이디/비밀번호 찾기 기능 구현
 */
@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendFindIdEmail(String toEmail, String userName, String uId) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("[YeDoc] 아이디 찾기 결과 안내");
        message.setText(
                "안녕하세요, " + userName + "님.\n\n" +
                        "요청하신 아이디는 [" + uId + "] 입니다.");
        mailSender.send(message);
    }

    public void sendTempPasswordEmail(String toEmail, String userName, String tempPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("[YeDoc] 임시 비밀번호 안내");
        message.setText(
                "안녕하세요, " + userName + "님.\n\n" +
                        "요청하신 임시 비밀번호는 [" + tempPassword + "] 입니다.\n" +
                        "로그인 후 반드시 비밀번호를 변경해주세요.");
        mailSender.send(message);
    }
}
