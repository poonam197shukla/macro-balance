CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_phone ON users (phone);

CREATE INDEX idx_products_category ON products (category_id);
CREATE INDEX idx_products_active ON products (is_active);
CREATE INDEX idx_products_name ON products (name);

CREATE INDEX idx_orders_user ON orders (user_id);
CREATE INDEX idx_orders_status ON orders (status);

CREATE INDEX idx_status_order ON order_status_history (order_id);

CREATE INDEX idx_refresh_user ON refresh_tokens (user_id);
CREATE INDEX idx_otp_email ON otp_requests (email);
CREATE INDEX idx_otp_phone ON otp_requests (phone);

CREATE INDEX idx_address_user ON addresses (user_id);
CREATE INDEX idx_payment_order ON payments (order_id);