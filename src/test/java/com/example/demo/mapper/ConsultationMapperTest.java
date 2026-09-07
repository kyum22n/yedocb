package com.example.demo.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.AbstractIntegrationTest;
import com.example.demo.dao.AdminConsultationDao;
import com.example.demo.dao.ConsultationDao;
import com.example.demo.dao.UserDao;
import com.example.demo.entity.Consultation;
import com.example.demo.entity.User;

/**
 * 파일명: ConsultationMapperTest.java
 * 설명: ConsultationMapper.xml / AdminConsultationMapper.xml을 실제 Postgres 대상으로 검증한다.
 *       상담 등록이 생성된 PK를 반환하는지, 상태별/회원별 조회, 취소 처리를 확인한다
 *       (상담→예약 전환 자체는 별도의 AdminConsultationConvertIntegrationTest에서 서비스
 *       계층까지 포함해 검증한다).
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성
 */
class ConsultationMapperTest extends AbstractIntegrationTest {

    @Autowired
    private ConsultationDao consultationDao;

    @Autowired
    private AdminConsultationDao adminConsultationDao;

    @Autowired
    private UserDao userDao;

    private String createUser(String id) {
        User user = new User();
        user.setUId(id);
        user.setUPwd("pwd");
        user.setUEmail(id + "@example.com");
        user.setUName("사용자");
        userDao.insertUser(user);
        return id;
    }

    @Test
    void 상담등록하면_생성된PK가_채워지고_기본상태는_RECEIVED이다() {
        String uId = createUser("consultuser1");
        Consultation consultation = new Consultation();
        consultation.setUId(uId);
        consultation.setConsultationMemo("상담 요청합니다");

        consultationDao.insertConsultation(consultation);

        assertThat(consultation.getConsultationId()).isNotNull();

        Consultation found = consultationDao.selectConsultationById(consultation.getConsultationId(), uId);
        assertThat(found.getConsultationStatus()).isEqualTo("RECEIVED");
    }

    @Test
    void 회원별상담목록조회가_동작한다() {
        String uId = createUser("consultuser2");
        Consultation c1 = new Consultation();
        c1.setUId(uId);
        consultationDao.insertConsultation(c1);
        Consultation c2 = new Consultation();
        c2.setUId(uId);
        consultationDao.insertConsultation(c2);

        List<Consultation> list = consultationDao.selectConsultationsByUId(uId);

        assertThat(list).hasSize(2);
    }

    @Test
    void 상담취소하면_상태가_CANCELED로바뀐다() {
        String uId = createUser("consultuser3");
        Consultation consultation = new Consultation();
        consultation.setUId(uId);
        consultationDao.insertConsultation(consultation);

        consultationDao.cancelConsultation(consultation.getConsultationId(), uId);

        Consultation found = consultationDao.selectConsultationById(consultation.getConsultationId(), uId);
        assertThat(found.getConsultationStatus()).isEqualTo("CANCELED");
    }

    @Test
    void 관리자용_상태별상담조회가_동작한다() {
        String uId = createUser("consultuser4");
        Consultation consultation = new Consultation();
        consultation.setUId(uId);
        consultationDao.insertConsultation(consultation);

        List<Consultation> received = adminConsultationDao.selectConsultationsByStatus("RECEIVED");

        assertThat(received).extracting(Consultation::getConsultationId)
                .contains(consultation.getConsultationId());
    }
}
