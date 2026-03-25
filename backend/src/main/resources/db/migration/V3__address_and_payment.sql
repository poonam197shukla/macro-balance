-- =========================
-- ADDRESSES
-- =========================
CREATE TABLE addresses
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT       NOT NULL,
    line1       VARCHAR(255) NOT NULL,
    line2       VARCHAR(255),
    city        VARCHAR(100) NOT NULL,
    state       VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20)  NOT NULL,
    country     VARCHAR(100) NOT NULL DEFAULT 'India',
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_address_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- =========================
-- PAYMENTS
-- =========================
CREATE TABLE payments
(
    id                 BIGSERIAL PRIMARY KEY,
    order_id           BIGINT         NOT NULL,
    gateway_order_id   VARCHAR(255),
    gateway_payment_id VARCHAR(255),
    status             VARCHAR(50)    NOT NULL,
    amount             NUMERIC(10, 2) NOT NULL,
    created_at         TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_payment_order
        FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE
);