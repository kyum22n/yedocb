package com.example.demo.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.AbstractIntegrationTest;
import com.example.demo.dao.AdminNoticeDao;
import com.example.demo.dao.NoticeDao;
import com.example.demo.entity.Notice;

/**
 * 파일명: NoticeMapperTest.java
 * 설명: NoticeMapper.xml / AdminNoticeMapper.xml을 실제 Postgres 대상으로 검증한다. 사용자용
 *       조회(NoticeDao)가 is_visible=FALSE인 글을 실제로 걸러내는지가 핵심이다.
 *
 * ===============================
 * 수정 이력
 * ===============================
 * 2026-09-07 | 테스트 | Phase 6 연동테스트 작성
 */
class NoticeMapperTest extends AbstractIntegrationTest {

    @Autowired
    private NoticeDao noticeDao;

    @Autowired
    private AdminNoticeDao adminNoticeDao;

    private Notice newNotice(String title, boolean visible) {
        Notice notice = new Notice();
        notice.setTitle(title);
        notice.setContent("내용");
        notice.setNoticeType("NOTICE");
        notice.setIsVisible(visible);
        return notice;
    }

    @Test
    void 노출설정된_공지만_사용자조회에_나온다() {
        adminNoticeDao.insertNotice(newNotice("보이는공지", true));
        adminNoticeDao.insertNotice(newNotice("숨김공지", false));

        List<Notice> visible = noticeDao.selectVisibleNotices();

        assertThat(visible).extracting(Notice::getTitle).contains("보이는공지");
        assertThat(visible).extracting(Notice::getTitle).doesNotContain("숨김공지");
    }

    @Test
    void 숨김공지는_ID로직접조회해도_사용자에게_보이지않는다() {
        Notice notice = newNotice("숨김상세", false);
        adminNoticeDao.insertNotice(notice);

        Notice found = noticeDao.selectVisibleNoticeById(notice.getNoticeId());

        assertThat(found).isNull();
    }

    @Test
    void 관리자조회는_숨김여부와_무관하게_전부_보인다() {
        adminNoticeDao.insertNotice(newNotice("관리자용1", true));
        adminNoticeDao.insertNotice(newNotice("관리자용2", false));

        List<Notice> all = adminNoticeDao.selectAllNotices();

        assertThat(all).extracting(Notice::getTitle).contains("관리자용1", "관리자용2");
    }

    @Test
    void isVisible_미지정시_기본값은_TRUE이다() {
        Notice notice = new Notice();
        notice.setTitle("기본값테스트");
        notice.setContent("내용");
        notice.setNoticeType("NOTICE");
        // isVisible 미설정 (null)

        adminNoticeDao.insertNotice(notice);

        Notice found = adminNoticeDao.selectNoticeById(notice.getNoticeId());
        assertThat(found.getIsVisible()).isTrue();
    }
}
