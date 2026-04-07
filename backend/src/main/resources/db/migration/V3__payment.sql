-- =========================
-- PAYMENTS
-- =========================
CREATE TABLE payments
(
    id                 BIGSERIAL PRIMARY KEY,
    order_id           BIGINT         NOT NULL,
    razorpay_order_id  VARCHAR(255),              -- created via Razorpay API
    razorpay_payment_id VARCHAR(255),             -- returned after payment
    razorpay_signature  VARCHAR(500),             -- for webhook verification
    status             VARCHAR(50)    NOT NULL DEFAULT 'PENDING',
    amount             NUMERIC(10, 2) NOT NULL,
    currency           VARCHAR(10)    NOT NULL DEFAULT 'INR',
    failure_reason     VARCHAR(500),              -- capture failure message
    created_at         TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_payment_order
        FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE
);