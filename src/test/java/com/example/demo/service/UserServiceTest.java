package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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
 * 파일명: UserServiceTest.java
 * 설명: UserService 단위 테스트 (Mockito, 실제 DB 미사용)
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | Phase 1 단위 테스트 작성
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MailService mailService;

    @InjectMocks
    private UserService userService;

    private UserCreateRequestDto createRequest;

    @BeforeEach
    void setUp() {
        createRequest = new UserCreateRequestDto();
        createRequest.setUId("testuser");
        createRequest.setUPwd("Password1!");
        createRequest.setUName("홍길동");
        createRequest.setUEmail("test@example.com");
        createRequest.setUPhone("010-1234-5678");
    }

    @Test
    void 회원가입_성공() {
        when(userDao.selectUserById("testuser")).thenReturn(null);
        when(userDao.selectUserByEmail("test@example.com")).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-pwd");
        when(userDao.insertUser(any(User.class))).thenReturn(1);

        int result = userService.createUser(createRequest);

        assertThat(result).isEqualTo(1);
        verify(userDao).insertUser(any(User.class));
    }

    @Test
    void 회원가입_아이디중복이면_DuplicateResourceException() {
        User existing = new User();
        existing.setUId("testuser");
        when(userDao.selectUserById("testuser")).thenReturn(existing);

        assertThatThrownBy(() -> userService.createUser(createRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("아이디");
    }

    @Test
    void 회원가입_이메일중복이면_DuplicateResourceException() {
        when(userDao.selectUserById("testuser")).thenReturn(null);
        User existing = new User();
        existing.setUEmail("test@example.com");
        when(userDao.selectUserByEmail("test@example.com")).thenReturn(existing);

        assertThatThrownBy(() -> userService.createUser(createRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("이메일");
    }

    @Test
    void 마이페이지조회_존재하지않으면_ResourceNotFoundException() {
        when(userDao.selectUserById("nouser")).thenReturn(null);

        assertThatThrownBy(() -> userService.getUserById("nouser"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void 마이페이지조회_성공시_비밀번호를_포함하지않는_DTO_반환() {
        User user = new User();
        user.setUId("testuser");
        user.setUPwd("encoded-pwd");
        user.setUName("홍길동");
        user.setUEmail("test@example.com");
        when(userDao.selectUserById("testuser")).thenReturn(user);

        UserMypageResponseDto response = userService.getUserById("testuser");

        assertThat(response.getUId()).isEqualTo("testuser");
        assertThat(response.getUName()).isEqualTo("홍길동");
    }

    @Test
    void 마이페이지수정_존재하지않으면_ResourceNotFoundException() {
        UserMypageUpdateRequestDto request = new UserMypageUpdateRequestDto();
        when(userDao.selectUserById("nouser")).thenReturn(null);

        assertThatThrownBy(() -> userService.modifyUser(request, "nouser"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void 회원탈퇴_존재하지않으면_ResourceNotFoundException() {
        when(userDao.selectUserById("nouser")).thenReturn(null);

        assertThatThrownBy(() -> userService.removeUser("nouser"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void 비밀번호변경_현재비밀번호불일치시_InvalidCredentialsException() {
        User user = new User();
        user.setUId("testuser");
        user.setUPwd("encoded-old-pwd");
        when(userDao.selectUserById("testuser")).thenReturn(user);
        when(passwordEncoder.matches("wrong", "encoded-old-pwd")).thenReturn(false);

        UserPasswordUpdateRequestDto request = new UserPasswordUpdateRequestDto();
        request.setCurrentPwd("wrong");
        request.setNewPwd("NewPassword1!");

        assertThatThrownBy(() -> userService.changePassword(request, "testuser"))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void 비밀번호변경_존재하지않으면_ResourceNotFoundException() {
        when(userDao.selectUserById("nouser")).thenReturn(null);

        UserPasswordUpdateRequestDto request = new UserPasswordUpdateRequestDto();
        request.setCurrentPwd("old");
        request.setNewPwd("NewPassword1!");

        assertThatThrownBy(() -> userService.changePassword(request, "nouser"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void 비밀번호변경_성공() {
        User user = new User();
        user.setUId("testuser");
        user.setUPwd("encoded-old-pwd");
        when(userDao.selectUserById("testuser")).thenReturn(user);
        when(passwordEncoder.matches("oldpwd", "encoded-old-pwd")).thenReturn(true);
        when(passwordEncoder.encode("NewPassword1!")).thenReturn("encoded-new-pwd");
        when(userDao.updatePassword("testuser", "encoded-new-pwd")).thenReturn(1);

        UserPasswordUpdateRequestDto request = new UserPasswordUpdateRequestDto();
        request.setCurrentPwd("oldpwd");
        request.setNewPwd("NewPassword1!");

        int result = userService.changePassword(request, "testuser");

        assertThat(result).isEqualTo(1);
        verify(userDao).updatePassword("testuser", "encoded-new-pwd");
    }

    @Test
    void 아이디찾기_존재하는이메일이면_해당이메일로_아이디를_발송한다() {
        User user = new User();
        user.setUId("testuser");
        user.setUName("홍길동");
        user.setUEmail("test@example.com");
        when(userDao.selectUserByEmail("test@example.com")).thenReturn(user);

        userService.findIdAndSendEmail("test@example.com");

        verify(mailService).sendFindIdEmail("test@example.com", "홍길동", "testuser");
    }

    @Test
    void 아이디찾기_존재하지않는이메일이면_메일을_보내지않고_예외도_없다() {
        when(userDao.selectUserByEmail("nouser@example.com")).thenReturn(null);

        userService.findIdAndSendEmail("nouser@example.com");

        verify(mailService, never()).sendFindIdEmail(anyString(), anyString(), anyString());
    }

    @Test
    void 비밀번호찾기_존재하는아이디면_임시비밀번호를_저장하고_이메일로_발송한다() {
        User user = new User();
        user.setUId("testuser");
        user.setUName("홍길동");
        user.setUEmail("test@example.com");
        when(userDao.selectUserById("testuser")).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-temp-pwd");

        userService.resetPasswordAndSendEmail("testuser");

        verify(userDao).updatePassword(eq("testuser"), eq("encoded-temp-pwd"));
        verify(mailService).sendTempPasswordEmail(eq("test@example.com"), eq("홍길동"), anyString());
    }

    @Test
    void 비밀번호찾기_존재하지않는아이디면_아무일도_하지않는다() {
        when(userDao.selectUserById("nouser")).thenReturn(null);

        userService.resetPasswordAndSendEmail("nouser");

        verify(userDao, never()).updatePassword(anyString(), anyString());
        verify(mailService, never()).sendTempPasswordEmail(anyString(), anyString(), anyString());
    }
}
