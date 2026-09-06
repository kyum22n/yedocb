-- =====================================================================
-- 파일명: schema.sql
-- 설명: Phase 1 도메인(User, Admin, Reservation, Notice, Inquiry)의
--       테이블 정의. PostgreSQL 문법 기준, 모두 CREATE TABLE IF NOT EXISTS
--       (idempotent / additive, DROP 문 없음).
--
-- =====================================================================
-- 수정 이력
-- =====================================================================
-- 2026-09-06 | 리팩토링 | Phase 1 스키마 초안 작성
-- =====================================================================

-- 회원 (구 member 테이블을 대체. uId가 로그인 ID이자 PK)
CREATE TABLE IF NOT EXISTS users (
    u_id        VARCHAR(20)     NOT NULL PRIMARY KEY,
    u_pwd       VARCHAR(200)    NOT NULL,
    u_email     VARCHAR(100)    NOT NULL UNIQUE,
    u_name      VARCHAR(50)     NOT NULL,
    u_phone     VARCHAR(20),
    u_birth     DATE,
    u_gender    VARCHAR(10),
    created_at  TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- 관리자
CREATE TABLE IF NOT EXISTS admin (
    admin_id        SERIAL          PRIMARY KEY,
    admin_login_id  VARCHAR(20)     NOT NULL UNIQUE,
    admin_password  VARCHAR(200)    NOT NULL,
    admin_name      VARCHAR(50)     NOT NULL,
    admin_email     VARCHAR(100)    NOT NULL,
    admin_phone     VARCHAR(20),
    admin_role      VARCHAR(20)     NOT NULL DEFAULT 'ADMIN',
    created_by      VARCHAR(20),
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- 예약 (memberId -> uId(String)로 변경, users.u_id 참조)
CREATE TABLE IF NOT EXISTS reservation (
    reservation_id      SERIAL          PRIMARY KEY,
    u_id                VARCHAR(20)     NOT NULL REFERENCES users(u_id),
    treatment_id        INTEGER,
    admin_id            INTEGER,
    reservation_date    DATE            NOT NULL,
    reservation_time    TIME            NOT NULL,
    reservation_status  VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    member_memo         TEXT,
    admin_memo          TEXT,
    pms_sync_status     VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    created_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- 공지/이벤트
CREATE TABLE IF NOT EXISTS notice (
    notice_id       SERIAL          PRIMARY KEY,
    title           VARCHAR(200)    NOT NULL,
    content         TEXT            NOT NULL,
    notice_type     VARCHAR(20)     NOT NULL,
    image_url       VARCHAR(500),
    is_visible      BOOLEAN         NOT NULL DEFAULT TRUE,
    start_at        TIMESTAMP,
    end_at          TIMESTAMP,
    created_by      INTEGER,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- 문의 (memberId -> uId(String)로 변경, users.u_id 참조)
CREATE TABLE IF NOT EXISTS inquiry (
    inquiry_id      SERIAL          PRIMARY KEY,
    u_id            VARCHAR(20)     NOT NULL REFERENCES users(u_id),
    inquiry_type    VARCHAR(20)     NOT NULL,
    title           VARCHAR(200)    NOT NULL,
    content         TEXT            NOT NULL,
    inquiry_status  VARCHAR(20)     NOT NULL DEFAULT 'WAITING',
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- 문의 답변
CREATE TABLE IF NOT EXISTS inquiry_answer (
    answer_id       SERIAL          PRIMARY KEY,
    inquiry_id      INTEGER         NOT NULL REFERENCES inquiry(inquiry_id),
    admin_id        INTEGER,
    answer_content  TEXT            NOT NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW()
);
