// package com.example.demo.service;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import com.example.demo.dao.AdminDao;
// import com.example.demo.entity.Admin;

// import java.util.List;
// import java.util.UUID;
// import org.springframework.mail.SimpleMailMessage;
// import org.springframework.mail.javamail.JavaMailSender;

// /**
//  * 파일명: AdminLoginService.java
//  * 설명: 관리자 로그인 관련 service
//  *
//  * ===============================
//  * 수정 이력
//  * ===============================
//  * 2026-05-02 | 규민 | 로그인 메소드 분리
//  */

// @Service
// public class AdminLoginService {
    
//     @Autowired
//     private AdminDao adminDao;

//     @Autowired
//     private JavaMailSender mailSender;

//     // 로그인 id로 관리자 조회
//     public Admin getAdminByLoginId(String aLoginId) {
//         return adminDao.selectAdminByLoginId(aLoginId);
//     }

//     // 관리자 이메일로 관리자 아이디 찾기
//     public String getAdminIdByEmail(String aEmail) {
//         return adminDao.selectAdminIdByEmail(aEmail);  
//     }

//     // 관리자 비밀번호 변경
//     public int modifyAdminPassword(Admin admin) {
//         return adminDao.updatePassword(admin);
//     }

//     // 임시 비밀번호 발급
//     public void sendTempPassword(Admin admin) {

//         // 1. 임시 비밀번호 생성
//         String tempPassword = UUID.randomUUID().toString().substring(0, 8);

//         // 2. 임시 비밀번호 암호화
//         PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//         String encodedPassword = passwordEncoder.encode(tempPassword);

//         // 3. DB에 암호화된 비밀번호 저장
//         admin.setAPassword(encodedPassword); // 암호화된 비밀번호 저장
//         adminDao.updatePassword(admin);

//         // 4. 이메일 전송(원본 임시 비밀번호는 메일로만 전달)
//         SimpleMailMessage message = new SimpleMailMessage();
//         message.setTo(admin.getAEmail());
//         message.setSubject("[서비스명] 임시 비밀번호 안내");
//         message.setText(
//                 "안녕하세요, " + admin.getAName() + "님.\n\n" +
//                         "요청하신 임시 비밀번호는 [" + tempPassword + "] 입니다.\n" +
//                         "로그인 후 반드시 비밀번호를 변경해주세요.");

//         mailSender.send(message);
//     }
// }
