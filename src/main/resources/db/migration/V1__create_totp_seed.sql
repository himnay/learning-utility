CREATE TABLE IF NOT EXISTS totp_seed (
    id           BIGSERIAL    PRIMARY KEY,
    account_name VARCHAR(100) NOT NULL UNIQUE,
    secret       VARCHAR(64)  NOT NULL,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE totp_seed IS 'One row per account holding its Base32 TOTP seed (com.learning.totp).';
