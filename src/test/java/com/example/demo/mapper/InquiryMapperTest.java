package com.example.demo.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.AbstractIntegrationTest;
import com.example.demo.dao.AdminInquiryDao;
import com.example.demo.dao.InquiryDao;
import com.example.demo.dao.UserDao;
import com.example.demo.entity.Inquiry;
import com.example.demo.entity.InquiryAnswer;
import com.example.demo.entity.User;

/**
 * 파일명: InquiryMapperTest.java
 * 설명: InquiryMapper.xml / AdminInquiryMapper.xml을 실제 Postgres 대상으로 검증한다.
 *       문의 등록의 PK 생성, 답변 등록/조회, 문의 상태 변경을 확인한다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성
 */
class InquiryMapperTest extends AbstractIntegrationTest {

    @Autowired
    private InquiryDao inquiryDao;

    @Autowired
    private AdminInquiryDao adminInquiryDao;

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
    void 문의등록하면_생성된PK가_채워지고_기본상태는_WAITING이다() {
        String uId = createUser("inquiryuser1");
        Inquiry inquiry = new Inquiry();
        inquiry.setUId(uId);
        inquiry.setInquiryType("GENERAL");
        inquiry.setTitle("문의합니다");
        inquiry.setContent("내용");

        inquiryDao.insertInquiry(inquiry);

        assertThat(inquiry.getInquiryId()).isNotNull();

        Inquiry found = inquiryDao.selectInquiryById(inquiry.getInquiryId(), uId);
        assertThat(found.getInquiryStatus()).isEqualTo("WAITING");
    }

    @Test
    void 답변을_등록하면_문의답변조회에서_확인된다() {
        String uId = createUser("inquiryuser2");
        Inquiry inquiry = new Inquiry();
        inquiry.setUId(uId);
        inquiry.setInquiryType("GENERAL");
        inquiry.setTitle("문의");
        inquiry.setContent("내용");
        inquiryDao.insertInquiry(inquiry);

        InquiryAnswer answer = new InquiryAnswer();
        answer.setInquiryId(inquiry.getInquiryId());
        answer.setAnswerContent("답변 드립니다");
        adminInquiryDao.insertInquiryAnswer(answer);

        InquiryAnswer found = inquiryDao.selectAnswerByInquiryId(inquiry.getInquiryId());
        assertThat(found).isNotNull();
        assertThat(found.getAnswerContent()).isEqualTo("답변 드립니다");
    }

    @Test
    void 문의상태수정이_반영된다() {
        String uId = createUser("inquiryuser3");
        Inquiry inquiry = new Inquiry();
        inquiry.setUId(uId);
        inquiry.setInquiryType("GENERAL");
        inquiry.setTitle("문의");
        inquiry.setContent("내용");
        inquiryDao.insertInquiry(inquiry);

        inquiry.setInquiryStatus("ANSWERED");
        adminInquiryDao.updateInquiryStatus(inquiry);

        Inquiry found = inquiryDao.selectInquiryById(inquiry.getInquiryId(), uId);
        assertThat(found.getInquiryStatus()).isEqualTo("ANSWERED");
    }
}
