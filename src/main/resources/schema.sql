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

-- =====================================================================
-- Phase 2 도메인(Consultation, Treatment/TreatmentCategory, Review)
-- =====================================================================
-- 2026-09-06 | 리팩토링 | Phase 2 스키마 추가 (기존 테이블 정의는 변경하지 않음, 추가만 함)
-- =====================================================================

-- 진료항목 카테고리 (TreatmentCategory.java 기준)
CREATE TABLE IF NOT EXISTS treatment_category (
    category_id     SERIAL          PRIMARY KEY,
    category_name   VARCHAR(100)    NOT NULL,
    is_visible      BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- 진료항목 (Treatment.java 기준, category_id -> treatment_category.category_id 참조)
CREATE TABLE IF NOT EXISTS treatment (
    treatment_id    SERIAL          PRIMARY KEY,
    category_id     INTEGER         REFERENCES treatment_category(category_id),
    treatment_name  VARCHAR(200)    NOT NULL,
    description     TEXT,
    is_reservable   BOOLEAN         NOT NULL DEFAULT TRUE,
    is_visible      BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- 상담 (Consultation.java 기준, u_id -> users.u_id 참조)
CREATE TABLE IF NOT EXISTS consultation (
    consultation_id     SERIAL          PRIMARY KEY,
    u_id                VARCHAR(20)     NOT NULL REFERENCES users(u_id),
    reservation_id      INTEGER         REFERENCES reservation(reservation_id),
    treatment_id        INTEGER         REFERENCES treatment(treatment_id),
    admin_id            INTEGER,
    consultation_status VARCHAR(20)     NOT NULL DEFAULT 'RECEIVED',
    consultation_memo   TEXT,
    preferred_date      DATE,
    preferred_time      TIME,
    created_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- 리뷰(후기) (Review.java 기준, treatment_id -> treatment.treatment_id, user_id -> users.u_id 참조)
CREATE TABLE IF NOT EXISTS review (
    review_id       SERIAL          PRIMARY KEY,
    treatment_id    INTEGER         REFERENCES treatment(treatment_id),
    user_id         VARCHAR(20)     NOT NULL REFERENCES users(u_id),
    title           VARCHAR(200)    NOT NULL,
    content         TEXT            NOT NULL,
    image_url       VARCHAR(500),
    hash_tag        VARCHAR(200),
    hits            INTEGER         NOT NULL DEFAULT 0,
    is_hidden       BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW()
);
