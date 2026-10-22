CREATE TYPE otp_channel AS ENUM ('SMS', 'EMAIL');

CREATE TYPE otp_status AS ENUM ('PENDING', 'VERIFIED', 'EXPIRED', 'FAILED');

CREATE TYPE audit_event_type AS ENUM (
    'OTP_GENERATED', 'OTP_SENT', 'OTP_VERIFIED',
    'OTP_FAILED', 'OTP_EXPIRED', 'OTP_RATE_LIMITED'
);

-- 1. users
CREATE TABLE users (
    id          BIGSERIAL    PRIMARY KEY,
    email       VARCHAR(255) UNIQUE,
    phone       VARCHAR(20)  UNIQUE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT users_contact_chk CHECK (email IS NOT NULL OR phone IS NOT NULL)
);

-- 2. otp_records
CREATE TABLE otp_records (
    id           BIGSERIAL    PRIMARY KEY,
    user_id      BIGINT       NOT NULL REFERENCES users(id),
    otp_hash     VARCHAR(255) NOT NULL,
    purpose      VARCHAR(30)  NOT NULL,
    channel      otp_channel  NOT NULL,
    status       otp_status   NOT NULL DEFAULT 'PENDING',
    attempts     SMALLINT     NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    expires_at   TIMESTAMPTZ  NOT NULL,
    verified_at  TIMESTAMPTZ,
    CONSTRAINT otp_purpose_chk CHECK (purpose IN ('LOGIN','SIGNUP','PASSWORD_RESET','PAYMENT')),
    CONSTRAINT otp_expiry_chk  CHECK (expires_at > created_at)
);

CREATE INDEX idx_otp_lookup  ON otp_records (user_id, purpose, status);
CREATE INDEX idx_otp_expires ON otp_records (expires_at);

-- Only one live (PENDING) code per user and purpose.
-- Mark the old code EXPIRED before creating a new one.
CREATE UNIQUE INDEX uq_otp_one_pending
    ON otp_records (user_id, purpose)
    WHERE status = 'PENDING';

-- 3. audit_logs
CREATE TABLE audit_logs (
    id          BIGSERIAL        PRIMARY KEY,
    user_id     BIGINT           REFERENCES users(id),
    event_type  audit_event_type NOT NULL,
    details     TEXT,
    ip_address  VARCHAR(45),
    created_at  TIMESTAMPTZ      NOT NULL DEFAULT now()
);

CREATE INDEX idx_audit_user_time ON audit_logs (user_id, created_at);
