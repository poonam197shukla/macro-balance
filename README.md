# MacroBalance Backend

A production-ready Spring Boot backend for MacroBalance — a health-focused e-commerce platform selling protein bars, date bites, roasted makhana, trail mixes, and more. Built with clean modular architecture, JWT authentication, Razorpay payments, and Flyway-managed migrations.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0.2 |
| Database | PostgreSQL 16 |
| ORM | Spring Data JPA / Hibernate |
| Migrations | Flyway |
| Auth | JWT (jjwt 0.13) + Spring Security |
| Payments | Razorpay |
| Docs | SpringDoc OpenAPI / Swagger UI |
| Build | Maven |
| Logging | Logback |

---

## Project Structure

```
com.macrobalance
├── auth          # Registration, login, OTP, refresh tokens
├── user          # User profile, password management
├── address       # Delivery address CRUD
├── cart          # Guest + user cart, cart merge on login
├── product       # Products, categories, nutrition, reviews
├── order         # Checkout, order history, status transitions
├── payment       # Razorpay integration, webhook handling
└── common        # BaseEntity, ApiResponse, global exception handler
```

---

## Prerequisites

- Java 21+
- PostgreSQL 16+
- Maven 3.9+
- Razorpay account (test mode for development)
- ngrok (for local webhook testing)

---

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/kaustubhkmishra/macroBalance/tree/main/backend
cd macrobalance-backend
```

### 2. Create the database

```sql
CREATE DATABASE macro_balance;
```

### 3. Configure application.yml

Update `src/main/resources/application.yml` with your local values:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/macro_balance
    username: your_postgres_username
    password: your_postgres_password

razorpay:
  key-id: rzp_test_xxxxxxxxxxxx
  key-secret: xxxxxxxxxxxxxxxxxxxx
  webhook-secret: your_webhook_secret
```

> **Never commit real credentials.** Use environment variables in production.

### 4. Run the application

```bash
./mvnw spring-boot:run
```

Flyway will automatically run all migrations on startup. The application starts on port `8080`.

---

## Database Migrations

Migrations live in `src/main/resources/db/migration/` and follow Flyway's versioned naming convention:

```
V1__init_core_tables.sql       # users, categories, products, nutrition,
                               # reviews, carts, addresses, orders
V2__auth_tables.sql            # refresh_tokens, otp_requests
V3__payment.sql                # payments
V4__indexes_and_constraints.sql
```

To reset the database during development:

```sql
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
```

Then restart the application — Flyway will recreate everything from scratch.

---

## API Documentation

Swagger UI is available at:

```
http://localhost:8080/swagger-ui.html
```

All protected endpoints require a Bearer token in the `Authorization` header:

```
Authorization: Bearer <your_jwt_token>
```

---

## API Reference

### Auth

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Register new user |
| POST | `/api/auth/login` | Public | Login with email + password |
| POST | `/api/auth/send-otp` | Public | Send OTP to email or phone |
| POST | `/api/auth/login/otp` | Public | Login with OTP |
| POST | `/api/auth/reset-password` | Public | Reset password via OTP |

### Users

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/users/me` | Required | Get current user profile |
| PUT | `/api/users/me` | Required | Update name or phone |
| PATCH | `/api/users/me/password` | Required | Change password |

### Addresses

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/addresses` | Required | List all addresses |
| POST | `/api/addresses` | Required | Add new address |
| PUT | `/api/addresses/{id}` | Required | Update address |
| DELETE | `/api/addresses/{id}` | Required | Delete address |
| PATCH | `/api/addresses/{id}/default` | Required | Set as default |

### Categories

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/categories` | Public | List active categories |
| GET | `/api/categories/{slug}` | Public | Get category by slug |
| POST | `/api/categories/admin` | Admin | Create category |
| PUT | `/api/categories/admin/{id}` | Admin | Update category |
| PATCH | `/api/categories/admin/{id}/deactivate` | Admin | Deactivate category |

### Products

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/products` | Public | List products with filters |
| GET | `/api/products/{slug}` | Public | Get product detail |
| GET | `/api/products/{id}/reviews` | Public | Get product reviews |
| POST | `/api/products/{id}/reviews` | Required | Submit review |
| PUT | `/api/products/{id}/reviews` | Required | Update own review |
| DELETE | `/api/products/{id}/reviews` | Required | Delete own review |
| POST | `/api/products/admin` | Admin | Create product |
| PUT | `/api/products/admin/{id}` | Admin | Update product |
| PATCH | `/api/products/admin/{id}/deactivate` | Admin | Deactivate product |

#### Product Filter Query Params

```
GET /api/products?categorySlug=protein-bar&minProtein=18&maxSugar=1&maxPrice=300&keyword=almond&page=0&size=12
```

### Cart

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/cart` | Optional | Get cart (guest or user) |
| POST | `/api/cart/items` | Optional | Add item to cart |
| PUT | `/api/cart/items/{itemId}` | Optional | Update item quantity |
| DELETE | `/api/cart/items/{itemId}` | Optional | Remove item |
| DELETE | `/api/cart` | Optional | Clear cart |

> Guest carts use the `X-Guest-Id` request header. On login, the guest cart merges into the user cart automatically.

### Orders

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/orders/checkout` | Required | Place order from cart |
| GET | `/api/orders` | Required | Get order history |
| GET | `/api/orders/{id}` | Required | Get order detail |
| PATCH | `/api/orders/admin/{id}/status` | Admin | Update order status |

#### Order Status Flow

```
PENDING → CONFIRMED → PROCESSING → SHIPPED → DELIVERED
PENDING → CANCELLED
CONFIRMED → CANCELLED
```

### Payments

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/payments/initiate` | Required | Create Razorpay order |
| POST | `/api/payments/webhook` | Public | Razorpay webhook handler |
| GET | `/api/payments/{orderId}` | Required | Get payment status |

---

## Payment Flow

```
1. POST /api/orders/checkout         → creates Order (PENDING)
2. POST /api/payments/initiate       → creates Razorpay order, returns razorpay_order_id
3. Frontend opens Razorpay modal     → user completes payment
4. Razorpay calls /api/payments/webhook
5. Webhook verifies HMAC signature   → updates Payment + transitions Order to CONFIRMED
```

---

## Local Webhook Testing with ngrok

Razorpay cannot reach `localhost` directly. Use ngrok to expose your local server:

```bash
# Start your app
./mvnw spring-boot:run

# In a separate terminal
ngrok http 8080
```

Copy the generated `https://` URL and set it as your webhook URL in the Razorpay dashboard:

```
https://your-ngrok-url.ngrok-free.app/api/payments/webhook
```

Monitor incoming webhook requests at `http://localhost:4040`.

**Test card details:**

```
Card Number : 4111 1111 1111 1111
Expiry      : Any future date
CVV         : Any 3 digits
UPI         : success@razorpay
```

---

## Logging

Logs are written to the `logs/` directory:

```
logs/
├── macro-balance-backend.log           # All INFO+ application logs
├── macro-balance-backend-error.log     # ERROR level only
├── macro-balance-backend-payments.log  # Payment events (90 day retention)
└── archived/                           # Compressed daily rollover files
```

Log levels by profile:

| Profile | Application Code | Hibernate SQL |
|---|---|---|
| `default` / `dev` | DEBUG | DEBUG |
| `prod` | INFO | OFF |

---

## Security Notes

- JWT tokens expire after **24 hours**
- OTPs expire after **5 minutes** and allow a maximum of **3 attempts**
- OTP records older than 1 hour are purged every hour via a scheduled job
- Passwords are hashed with **BCrypt**
- Webhook authenticity is verified via **HMAC-SHA256** signature
- Admin endpoints are protected with `@PreAuthorize("hasRole('ADMIN')")`

---

## Environment Variables (Production)

Instead of hardcoding secrets in `application.yml`, use environment variables in production:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://your-host:5432/macro_balance
export SPRING_DATASOURCE_USERNAME=your_user
export SPRING_DATASOURCE_PASSWORD=your_password
export RAZORPAY_KEY_ID=rzp_live_xxxxxxxxxxxx
export RAZORPAY_KEY_SECRET=xxxxxxxxxxxxxxxxxxxx
export RAZORPAY_WEBHOOK_SECRET=your_webhook_secret
```

---

## Running Tests

```bash
./mvnw test
```

---

## Building for Production

```bash
./mvnw clean package -DskipTests
java -jar target/macro-balance-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

---

## Contributing

1. Create a feature branch: `git checkout -b feature/your-feature`
2. Commit your changes: `git commit -m "Add your feature"`
3. Push and open a pull request

---

## License

This project is proprietary. All rights reserved.
