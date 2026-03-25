-- =========================
-- REFRESH TOKENS
-- =========================
CREATE TABLE refresh_tokens
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT       NOT NULL,
    token       VARCHAR(500) NOT NULL UNIQUE,
    expiry_date TIMESTAMPTZ  NOT NULL,
    revoked     BOOLEAN               DEFAULT FALSE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_refresh_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- =========================
-- OTP REQUESTS
-- =========================
CREATE TABLE otp_requests
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT,
    email      VARCHAR(255),
    phone      VARCHAR(20),
    hashed_otp VARCHAR(255) NOT NULL,
    attempts   INTEGER               DEFAULT 0,
    expires_at TIMESTAMPTZ  NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_otp_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);