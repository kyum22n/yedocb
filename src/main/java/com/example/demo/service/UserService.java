package com.example.demo.service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dao.UserDao;
import com.example.demo.dto.request.user.UserCreateRequestDto;
import com.example.demo.dto.request.user.UserMypageUpdateRequestDto;
import com.example.demo.dto.request.user.UserPasswordUpdateRequestDto;
import com.example.demo.dto.response.user.UserMypageResponseDto;
import com.example.demo.entity.User;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.InvalidCredentialsException;
import com.example.demo.exception.ResourceNotFoundException;

/**
 * 파일명: UserService.java
 * 설명: 회원 정보 관련 서비스 클래스 (구 MemberService 대체)
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | Member -> User 통합 (Phase 1). IllegalArgumentException을
 *                        커스텀 예외(DuplicateResourceException/ResourceNotFoundException)로 교체,
 *                        응답 DTO 매핑을 XxxResponseDto.from(entity) 정적 팩토리 방식으로 변경
 */
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    private static final String TEMP_PASSWORD_LETTERS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
    private static final String TEMP_PASSWORD_DIGITS = "23456789";
    private static final String TEMP_PASSWORD_SPECIALS = "!@#$%*?&";

    // 회원가입
    public int createUser(UserCreateRequestDto request) {

        // 로그인 ID 중복 체크
        if (userDao.selectUserById(request.getUId()) != null) {
            throw new DuplicateResourceException("이미 존재하는 아이디입니다.");
        }

        // 이메일 중복 체크
        if (userDao.selectUserByEmail(request.getUEmail()) != null) {
            throw new DuplicateResourceException("이미 존재하는 이메일입니다.");
        }

        User newUser = new User();
        newUser.setUId(request.getUId());
        newUser.setUPwd(passwordEncoder.encode(request.getUPwd()));
        newUser.setUName(request.getUName());
        newUser.setUEmail(request.getUEmail());
        newUser.setUPhone(request.getUPhone());
        newUser.setUBirth(request.getUBirth());
        newUser.setUGender(request.getUGender());

        return userDao.insertUser(newUser);
    }

    // 마이페이지 조회
    public UserMypageResponseDto getUserById(String uId) {

        User user = userDao.selectUserById(uId);

        if (user == null) {
            throw new ResourceNotFoundException("존재하지 않는 회원입니다.");
        }

        return UserMypageResponseDto.from(user);
    }

    // 마이페이지 수정 (수정 대상은 인증된 본인)
    public int modifyUser(UserMypageUpdateRequestDto request, String authenticatedUserId) {

        if (userDao.selectUserById(authenticatedUserId) == null) {
            throw new ResourceNotFoundException("존재하지 않는 회원입니다.");
        }

        User updatedUser = new User();
        updatedUser.setUId(authenticatedUserId);
        updatedUser.setUName(request.getUName());
        updatedUser.setUPhone(request.getUPhone());
        updatedUser.setUBirth(request.getUBirth());
        updatedUser.setUGender(request.getUGender());

        return userDao.updateUser(updatedUser);
    }

    // 비밀번호 변경 (대상은 인증된 본인, 현재 비밀번호 확인 필요)
    public int changePassword(UserPasswordUpdateRequestDto request, String authenticatedUserId) {

        User user = userDao.selectUserById(authenticatedUserId);

        if (user == null) {
            throw new ResourceNotFoundException("존재하지 않는 회원입니다.");
        }

        if (!passwordEncoder.matches(request.getCurrentPwd(), user.getUPwd())) {
            throw new InvalidCredentialsException("현재 비밀번호가 올바르지 않습니다.");
        }

        return userDao.updatePassword(authenticatedUserId, passwordEncoder.encode(request.getNewPwd()));
    }

    // 회원 탈퇴
    public int removeUser(String uId) {

        if (userDao.selectUserById(uId) == null) {
            throw new ResourceNotFoundException("존재하지 않는 회원입니다.");
        }

        return userDao.deleteUser(uId);
    }

    // 아이디 찾기 - 입력한 이메일로 회원이 존재하면 아이디를 이메일로 발송한다.
    // 이메일 존재 여부를 응답으로 노출하지 않기 위해 회원이 없어도 예외를 던지지 않고 조용히 종료한다.
    public void findIdAndSendEmail(String uEmail) {

        User user = userDao.selectUserByEmail(uEmail);

        if (user != null) {
            mailService.sendFindIdEmail(user.getUEmail(), user.getUName(), user.getUId());
        }
    }

    // 비밀번호 찾기(재발급) - 입력한 아이디로 회원이 존재하면 임시 비밀번호를 생성해 저장하고
    // 등록된 이메일로 발송한다. 아이디 존재 여부를 응답으로 노출하지 않기 위해 회원이 없어도
    // 예외를 던지지 않고 조용히 종료한다.
    public void resetPasswordAndSendEmail(String uId) {

        User user = userDao.selectUserById(uId);

        if (user != null) {
            String tempPassword = generateTempPassword();
            userDao.updatePassword(uId, passwordEncoder.encode(tempPassword));
            mailService.sendTempPasswordEmail(user.getUEmail(), user.getUName(), tempPassword);
        }
    }

    // 영문/숫자/특수문자를 모두 포함하는 10자리 임시 비밀번호 생성 (User 엔티티의 비밀번호 복잡도 규칙 충족)
    private String generateTempPassword() {

        SecureRandom random = new SecureRandom();
        List<Character> chars = new ArrayList<>();

        chars.add(TEMP_PASSWORD_LETTERS.charAt(random.nextInt(TEMP_PASSWORD_LETTERS.length())));
        chars.add(TEMP_PASSWORD_DIGITS.charAt(random.nextInt(TEMP_PASSWORD_DIGITS.length())));
        chars.add(TEMP_PASSWORD_SPECIALS.charAt(random.nextInt(TEMP_PASSWORD_SPECIALS.length())));

        String all = TEMP_PASSWORD_LETTERS + TEMP_PASSWORD_DIGITS + TEMP_PASSWORD_SPECIALS;
        for (int i = 0; i < 7; i++) {
            chars.add(all.charAt(random.nextInt(all.length())));
        }

        Collections.shuffle(chars, random);

        StringBuilder result = new StringBuilder();
        chars.forEach(result::append);
        return result.toString();
    }
}
