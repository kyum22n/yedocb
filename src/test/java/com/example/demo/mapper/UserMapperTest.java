package com.example.demo.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.AbstractIntegrationTest;
import com.example.demo.dao.UserDao;
import com.example.demo.entity.User;

/**
 * 파일명: UserMapperTest.java
 * 설명: UserMapper.xml을 실제 Postgres(Testcontainers) schema.sql 대상으로 검증하는 연동테스트.
 *       users 테이블 컬럼명(u_id/u_pwd/u_email 등 snake_case)과 엔티티 camelCase 매핑, unique
 *       제약(u_email) 동작을 확인한다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성
 */
class UserMapperTest extends AbstractIntegrationTest {

    @Autowired
    private UserDao userDao;

    private User newUser(String id, String email) {
        User user = new User();
        user.setUId(id);
        user.setUPwd("encoded-pwd");
        user.setUEmail(email);
        user.setUName("홍길동");
        user.setUPhone("010-1234-5678");
        user.setUBirth(LocalDate.of(1990, 1, 1));
        user.setUGender("F");
        return user;
    }

    @Test
    void 회원_등록후_ID로_조회하면_저장한값이_그대로_나온다() {
        userDao.insertUser(newUser("mapperuser1", "mapperuser1@example.com"));

        User found = userDao.selectUserById("mapperuser1");

        assertThat(found).isNotNull();
        assertThat(found.getUEmail()).isEqualTo("mapperuser1@example.com");
        assertThat(found.getUName()).isEqualTo("홍길동");
        assertThat(found.getUBirth()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(found.getCreatedAt()).isNotNull();
    }

    @Test
    void 이메일로_조회할수있다() {
        userDao.insertUser(newUser("mapperuser2", "mapperuser2@example.com"));

        User found = userDao.selectUserByEmail("mapperuser2@example.com");

        assertThat(found).isNotNull();
        assertThat(found.getUId()).isEqualTo("mapperuser2");
    }

    @Test
    void 중복된_이메일로_등록하면_DB_unique제약에_의해_예외가발생한다() {
        userDao.insertUser(newUser("mapperuser3", "dup@example.com"));

        org.assertj.core.api.Assertions.assertThatThrownBy(
                        () -> userDao.insertUser(newUser("mapperuser4", "dup@example.com")))
                .isInstanceOf(org.springframework.dao.DuplicateKeyException.class);
    }

    @Test
    void 회원정보수정과_비밀번호변경이_반영된다() {
        userDao.insertUser(newUser("mapperuser5", "mapperuser5@example.com"));

        User update = userDao.selectUserById("mapperuser5");
        update.setUName("변경된이름");
        update.setUPhone("010-9999-9999");
        userDao.updateUser(update);

        userDao.updatePassword("mapperuser5", "new-encoded-pwd");

        User found = userDao.selectUserById("mapperuser5");
        assertThat(found.getUName()).isEqualTo("변경된이름");
        assertThat(found.getUPwd()).isEqualTo("new-encoded-pwd");
    }

    @Test
    void 회원삭제하면_더이상_조회되지않는다() {
        userDao.insertUser(newUser("mapperuser6", "mapperuser6@example.com"));

        userDao.deleteUser("mapperuser6");

        assertThat(userDao.selectUserById("mapperuser6")).isNull();
    }
}
