package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.dao.UserDao;
import com.example.demo.dto.request.user.UserCreateRequestDto;
import com.example.demo.dto.response.user.AdminUserDetailResponseDto;
import com.example.demo.dto.response.user.AdminUserListResponseDto;
import com.example.demo.entity.User;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.ResourceNotFoundException;

/**
 * 파일명: AdminUserServiceTest.java
 * 설명: AdminUserService 단위 테스트 (Mockito, 실제 DB 미사용)
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-06 | 리팩토링 | Phase 1 단위 테스트 작성
 */
@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminUserService adminUserService;

    @Test
    void 관리자_회원등록_성공() {
        UserCreateRequestDto request = new UserCreateRequestDto();
        request.setUId("newuser");
        request.setUPwd("Password1!");
        request.setUName("홍길동");
        request.setUEmail("new@example.com");

        when(userDao.selectUserById("newuser")).thenReturn(null);
        when(userDao.selectUserByEmail("new@example.com")).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-pwd");
        when(userDao.insertUser(any(User.class))).thenReturn(1);

        int result = adminUserService.createAdminUser(request);

        assertThat(result).isEqualTo(1);
    }

    @Test
    void 관리자_회원등록_아이디중복이면_DuplicateResourceException() {
        UserCreateRequestDto request = new UserCreateRequestDto();
        request.setUId("dupuser");
        request.setUEmail("dup@example.com");

        when(userDao.selectUserById("dupuser")).thenReturn(new User());

        assertThatThrownBy(() -> adminUserService.createAdminUser(request))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void 회원목록조회는_비밀번호가_포함되지않은_DTO리스트를_반환한다() {
        User user1 = new User();
        user1.setUId("user1");
        user1.setUPwd("secret-hash-1");
        user1.setUName("사용자1");
        user1.setUEmail("user1@example.com");
        user1.setCreatedAt(LocalDateTime.now());

        User user2 = new User();
        user2.setUId("user2");
        user2.setUPwd("secret-hash-2");
        user2.setUName("사용자2");
        user2.setUEmail("user2@example.com");
        user2.setCreatedAt(LocalDateTime.now());

        when(userDao.selectAllUsers()).thenReturn(List.of(user1, user2));

        List<AdminUserListResponseDto> result = adminUserService.getAllUsers();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(AdminUserListResponseDto::getUId)
                .containsExactly("user1", "user2");
        // AdminUserListResponseDto에는 비밀번호 필드 자체가 존재하지 않으므로
        // 응답에 비밀번호 해시가 노출될 수 없음을 별도 필드 목록으로 확인한다.
        assertThat(AdminUserListResponseDto.class.getDeclaredFields())
                .noneMatch(f -> f.getName().toLowerCase().contains("pwd"));
    }

    @Test
    void 회원상세조회_존재하지않으면_ResourceNotFoundException() {
        when(userDao.selectUserById("nouser")).thenReturn(null);

        assertThatThrownBy(() -> adminUserService.getAdminUserById("nouser"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void 회원상세조회_성공() {
        User user = new User();
        user.setUId("testuser");
        user.setUName("홍길동");
        when(userDao.selectUserById("testuser")).thenReturn(user);

        AdminUserDetailResponseDto response = adminUserService.getAdminUserById("testuser");

        assertThat(response.getUId()).isEqualTo("testuser");
    }

    @Test
    void 회원삭제_존재하지않으면_ResourceNotFoundException() {
        when(userDao.selectUserById("nouser")).thenReturn(null);

        assertThatThrownBy(() -> adminUserService.removeAdminUser("nouser"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
