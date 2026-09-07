package com.example.demo.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.AbstractIntegrationTest;
import com.example.demo.dao.AdminTreatmentDao;
import com.example.demo.dao.TreatmentDao;
import com.example.demo.entity.Treatment;

/**
 * 파일명: TreatmentMapperTest.java
 * 설명: TreatmentMapper.xml / AdminTreatmentMapper.xml을 실제 Postgres 대상으로 검증한다.
 *       (category_id는 스키마상 NOT NULL이 아니므로 카테고리 없이도 등록 가능함을 함께 확인한다 —
 *       AdminTreatmentCategoryMapper.insertCategory가 깨져 있는 현재 상태에서도 이 테스트는
 *       영향받지 않는다.)
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성
 */
class TreatmentMapperTest extends AbstractIntegrationTest {

    @Autowired
    private TreatmentDao treatmentDao;

    @Autowired
    private AdminTreatmentDao adminTreatmentDao;

    private Treatment newTreatment(String name, boolean visible) {
        Treatment treatment = new Treatment();
        treatment.setTreatmentName(name);
        treatment.setDescription("설명");
        treatment.setIsReservable(true);
        treatment.setIsVisible(visible);
        return treatment;
    }

    @Test
    void 진료항목등록하면_생성된PK가_채워진다() {
        Treatment treatment = newTreatment("보톡스", true);

        adminTreatmentDao.insertTreatment(treatment);

        assertThat(treatment.getTreatmentId()).isNotNull();
    }

    @Test
    void 노출항목만_사용자조회에_나온다() {
        adminTreatmentDao.insertTreatment(newTreatment("보이는시술", true));
        adminTreatmentDao.insertTreatment(newTreatment("숨김시술", false));

        List<Treatment> visible = treatmentDao.selectVisibleTreatments();

        assertThat(visible).extracting(Treatment::getTreatmentName).contains("보이는시술");
        assertThat(visible).extracting(Treatment::getTreatmentName).doesNotContain("숨김시술");
    }

    @Test
    void 진료항목수정_삭제가_반영된다() {
        Treatment treatment = newTreatment("필러", true);
        adminTreatmentDao.insertTreatment(treatment);

        treatment.setTreatmentName("필러(수정)");
        adminTreatmentDao.updateTreatment(treatment);
        assertThat(adminTreatmentDao.selectTreatmentById(treatment.getTreatmentId()).getTreatmentName())
                .isEqualTo("필러(수정)");

        adminTreatmentDao.deleteTreatment(treatment.getTreatmentId());
        assertThat(adminTreatmentDao.selectTreatmentById(treatment.getTreatmentId())).isNull();
    }
}
