-- =========================
-- USERS
-- =========================
CREATE TABLE users
(
    id                BIGSERIAL PRIMARY KEY,
    name              VARCHAR(150) NOT NULL,
    email             VARCHAR(255) NOT NULL UNIQUE,
    phone             VARCHAR(20) UNIQUE,
    password          VARCHAR(255),
    role              VARCHAR(50)  NOT NULL DEFAULT 'USER',
    is_email_verified BOOLEAN               DEFAULT FALSE,
    is_phone_verified BOOLEAN               DEFAULT FALSE,
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by        VARCHAR(255) NOT NULL,
    updated_by        VARCHAR(255) NOT NULL,
    version           BIGINT                DEFAULT 0
);

-- =========================
-- CATEGORIES
-- =========================
CREATE TABLE categories
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(150) NOT NULL UNIQUE,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- =========================
-- PRODUCTS
-- =========================
CREATE TABLE products
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255)   NOT NULL,
    description TEXT,
    price       NUMERIC(10, 2) NOT NULL CHECK (price >= 0),
    protein     INTEGER                 DEFAULT 0,
    fiber       INTEGER                 DEFAULT 0,
    sugar       INTEGER                 DEFAULT 0,
    stock       INTEGER        NOT NULL CHECK (stock >= 0),
    category_id BIGINT         NOT NULL,
    is_active   BOOLEAN                 DEFAULT TRUE,
    created_at  TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_product_category
        FOREIGN KEY (category_id) REFERENCES categories (id)
);

-- =========================
-- CARTS
-- =========================
CREATE TABLE carts
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT,
    guest_id   VARCHAR(255),
    status     VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',

    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    version    BIGINT                DEFAULT 0,
        CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES users (id)
);

-- =========================
-- CART ITEMS
-- =========================
CREATE TABLE cart_items
(
    id         BIGSERIAL PRIMARY KEY,
    cart_id    BIGINT       NOT NULL,
    product_id BIGINT       NOT NULL,
    quantity   INTEGER      NOT NULL CHECK (quantity > 0),
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    version    BIGINT                DEFAULT 0,
    CONSTRAINT fk_cartitem_cart
        FOREIGN KEY (cart_id) REFERENCES carts (id) ON DELETE CASCADE,
    CONSTRAINT fk_cartitem_product
        FOREIGN KEY (product_id) REFERENCES products (id),
    CONSTRAINT unique_cart_product UNIQUE (cart_id, product_id)
);

-- =========================
-- ORDERS
-- =========================
CREATE TABLE orders
(
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT         NOT NULL,
    total_amount NUMERIC(10, 2) NOT NULL CHECK (total_amount >= 0),
    status       VARCHAR(50)    NOT NULL,
    created_at   TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_order_user
        FOREIGN KEY (user_id) REFERENCES users (id)
);

-- =========================
-- ORDER ITEMS
-- =========================
CREATE TABLE order_items
(
    id                BIGSERIAL PRIMARY KEY,
    order_id          BIGINT         NOT NULL,
    product_id        BIGINT         NOT NULL,
    quantity          INTEGER        NOT NULL CHECK (quantity > 0),
    price_at_purchase NUMERIC(10, 2) NOT NULL,
    created_at        TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_orderitem_order
        FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    CONSTRAINT fk_orderitem_product
        FOREIGN KEY (product_id) REFERENCES products (id)
);

-- =========================
-- ORDER STATUS HISTORY
-- =========================
CREATE TABLE order_status_history
(
    id         BIGSERIAL PRIMARY KEY,
    order_id   BIGINT      NOT NULL,
    old_status VARCHAR(50),
    new_status VARCHAR(50) NOT NULL,
    changed_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    changed_by VARCHAR(100),
    CONSTRAINT fk_status_order
        FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE
);