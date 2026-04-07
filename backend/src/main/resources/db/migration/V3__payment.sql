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