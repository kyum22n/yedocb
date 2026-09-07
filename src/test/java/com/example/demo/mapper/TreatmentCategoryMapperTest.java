package com.example.demo.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.AbstractIntegrationTest;
import com.example.demo.dao.AdminTreatmentCategoryDao;
import com.example.demo.dao.TreatmentCategoryDao;
import com.example.demo.entity.TreatmentCategory;

/**
 * 파일명: TreatmentCategoryMapperTest.java
 * 설명: TreatmentCategoryMapper.xml / AdminTreatmentCategoryMapper.xml을 실제 Postgres 대상으로
 *       검증한다. 노출여부(is_visible) 필터링이 사용자 조회에 실제로 적용되는지 확인한다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성 — 아래 등록 테스트에서 AdminTreatmentCategoryMapper.xml의
 *                        insertCategory SQL 문법 오류를 발견함(docs/test-report.md 참고, 운영 코드
 *                        수정은 사용자 확인 후 별도 진행)
 * 2026-09-07 | 배포 후 디버깅 | Phase 11 실사용 테스트에서 카테고리 등록 불가로 확인되어 SQL 수정
 *                        (docs/deployment-migration.md 참고), 아래 테스트를 성공 검증으로 변경
 */
class TreatmentCategoryMapperTest extends AbstractIntegrationTest {

    @Autowired
    private TreatmentCategoryDao categoryDao;

    @Autowired
    private AdminTreatmentCategoryDao adminCategoryDao;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @Test
    void 노출카테고리만_사용자조회에_나온다() {
        // AdminTreatmentCategoryMapper.xml의 insertCategory SQL이 깨져 있어(아래 별도 테스트 참고)
        // insertCategory로는 데이터를 만들 수 없다. 필터링 로직 자체(WHERE is_visible = TRUE)를
        // 검증하기 위해 JdbcTemplate으로 직접 두 행을 넣는다.
        insertCategoryDirectly("보이는카테고리", true);
        insertCategoryDirectly("숨김카테고리", false);

        List<TreatmentCategory> visible = categoryDao.selectVisibleCategories();

        assertThat(visible).extracting(TreatmentCategory::getCategoryName).contains("보이는카테고리");
        assertThat(visible).extracting(TreatmentCategory::getCategoryName).doesNotContain("숨김카테고리");
    }

    @Test
    void 카테고리등록_insertCategory가_정상적으로_생성된다() {
        // 배포 후 디버깅: 컬럼 목록에 COALESCE(...) 표현식이 그대로 들어가 있고 파라미터명도
        // 스네이크케이스(#{is_visible})로 잘못 적혀 있던 SQL을 수정함(AdminTreatmentCategoryMapper.xml).
        TreatmentCategory category = new TreatmentCategory();
        category.setCategoryName("성형");
        category.setIsVisible(true);

        adminCategoryDao.insertCategory(category);

        assertThat(category.getCategoryId()).isNotNull();
        TreatmentCategory saved = categoryDao.selectVisibleCategories().stream()
                .filter(c -> c.getCategoryId().equals(category.getCategoryId()))
                .findFirst()
                .orElseThrow();
        assertThat(saved.getCategoryName()).isEqualTo("성형");
        assertThat(saved.getIsVisible()).isTrue();
    }

    @Test
    void 카테고리등록_isVisible이_null이면_기본값_true로_저장된다() {
        TreatmentCategory category = new TreatmentCategory();
        category.setCategoryName("피부");
        category.setIsVisible(null);

        adminCategoryDao.insertCategory(category);

        TreatmentCategory saved = adminCategoryDao.selectCategoryById(category.getCategoryId());
        assertThat(saved.getIsVisible()).isTrue();
    }

    private void insertCategoryDirectly(String name, boolean visible) {
        jdbcTemplate.update(
                "INSERT INTO treatment_category (category_name, is_visible, created_at, updated_at) "
                        + "VALUES (?, ?, NOW(), NOW())",
                name, visible);
    }
}
