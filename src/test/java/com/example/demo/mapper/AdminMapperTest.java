package com.example.demo.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.AbstractIntegrationTest;
import com.example.demo.dao.AdminDao;
import com.example.demo.dto.request.admin.AdminUpdateRequestDto;
import com.example.demo.entity.Admin;

/**
 * 파일명: AdminMapperTest.java
 * 설명: AdminMapper.xml을 실제 Postgres 대상으로 검증하는 연동테스트. admin 테이블의
 *       admin_login_id UNIQUE 제약과, updateAdmin이 엔티티가 아니라 AdminUpdateRequestDto를
 *       파라미터로 받는(dao/entity 불일치) 구조가 실제로도 문제없이 동작하는지 확인한다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성
 */
class AdminMapperTest extends AbstractIntegrationTest {

    @Autowired
    private AdminDao adminDao;

    private Admin newAdmin(String loginId) {
        Admin admin = new Admin();
        admin.setAdminLoginId(loginId);
        admin.setAdminPassword("encoded-pwd");
        admin.setAdminName("관리자");
        admin.setAdminEmail(loginId + "@example.com");
        admin.setAdminRole("ADMIN");
        return admin;
    }

    @Test
    void 관리자등록후_로그인ID로_조회할수있다() {
        adminDao.insertAdmin(newAdmin("mapperadmin1"));

        Admin found = adminDao.selectAdminByLoginId("mapperadmin1");

        assertThat(found).isNotNull();
        assertThat(found.getAdminEmail()).isEqualTo("mapperadmin1@example.com");
        assertThat(found.getAdminRole()).isEqualTo("ADMIN");
    }

    @Test
    void 관리자ID로_상세조회할수있다() {
        adminDao.insertAdmin(newAdmin("mapperadmin2"));
        Admin inserted = adminDao.selectAdminByLoginId("mapperadmin2");

        Admin found = adminDao.selectAdminById(inserted.getAdminId());

        assertThat(found.getAdminLoginId()).isEqualTo("mapperadmin2");
    }

    @Test
    void 동일한_로그인ID로_등록하면_unique제약에_의해_예외가발생한다() {
        adminDao.insertAdmin(newAdmin("dupadmin"));

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> adminDao.insertAdmin(newAdmin("dupadmin")))
                .isInstanceOf(org.springframework.dao.DuplicateKeyException.class);
    }

    @Test
    void 관리자정보수정이_반영된다() {
        adminDao.insertAdmin(newAdmin("mapperadmin3"));
        Admin inserted = adminDao.selectAdminByLoginId("mapperadmin3");

        AdminUpdateRequestDto request = new AdminUpdateRequestDto();
        request.setAdminId(inserted.getAdminId());
        request.setAdminName("수정된이름");
        request.setAdminEmail("changed@example.com");
        adminDao.updateAdmin(request);

        Admin found = adminDao.selectAdminById(inserted.getAdminId());
        assertThat(found.getAdminName()).isEqualTo("수정된이름");
        assertThat(found.getAdminEmail()).isEqualTo("changed@example.com");
    }

    @Test
    void 관리자삭제하면_더이상_조회되지않는다() {
        adminDao.insertAdmin(newAdmin("mapperadmin4"));
        Admin inserted = adminDao.selectAdminByLoginId("mapperadmin4");

        adminDao.deleteAdmin(inserted.getAdminId());

        assertThat(adminDao.selectAdminById(inserted.getAdminId())).isNull();
    }
}
