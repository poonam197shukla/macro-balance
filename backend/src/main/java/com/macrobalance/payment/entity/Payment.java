package com.macrobalance.payment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * JPA entity representing a payment attempt for an order.
 *
 * <p>A payment record is created when {@code POST /api/payments/initiate}
 * is called. It is updated by the Razorpay webhook when the payment
 * succeeds or fails.
 *
 * <p>The relationship between IDs:
 * <ul>
 *   <li>{@code orderId} — MacroBalance internal order ID</li>
 *   <li>{@code razorpayOrderId} — ID created by Razorpay on initiation,
 *       used to open the payment modal</li>
 *   <li>{@code razorpayPaymentId} — ID assigned by Razorpay after the
 *       user completes payment, received via webhook</li>
 *   <li>{@code razorpaySignature} — HMAC-SHA256 signature from the webhook,
 *       stored for audit purposes after verification</li>
 * </ul>
 *
 * <p>All payment events are written to the dedicated audit log at
 * {@code logs/macro-balance-backend-payments.log} with 90-day retention.
 */
@Entity
@Getter
@Setter
@Table(name = "payments")
public class Payment {

    /** Primary key, auto-incremented. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * MacroBalance internal order ID this payment is for.
     * One-to-one with an order in the happy path, though multiple
     * payment attempts per order are technically possible.
     */
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    /**
     * Razorpay order ID returned by the Razorpay API during initiation.
     * Passed to the frontend to open the payment modal.
     * Format: {@code order_*}.
     */
    @Column(name = "razorpay_order_id")
    private String razorpayOrderId;

    /**
     * Razorpay payment ID received in the webhook payload after
     * the user completes payment. Null until payment is attempted.
     * Format: {@code pay_*}.
     */
    @Column(name = "razorpay_payment_id")
    private String razorpayPaymentId;

    /**
     * HMAC-SHA256 signature from the Razorpay webhook.
     * Verified against the raw payload using the webhook secret before
     * any processing occurs. Stored for audit trail after verification.
     */
    @Column(name = "razorpay_signature", length = 500)
    private String razorpaySignature;

    /**
     * Current status of this payment.
     * Defaults to {@link PaymentStatus#PENDING} on creation.
     * Updated to {@link PaymentStatus#SUCCESS} or {@link PaymentStatus#FAILED}
     * via the Razorpay webhook.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    /**
     * Payment amount in INR (rupees).
     * Stored in rupees — converted to paise (×100) only when
     * creating the Razorpay order via the API.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * Currency code for this payment.
     * Always {@code "INR"} — MacroBalance currently ships only within India.
     */
    @Column(nullable = false, length = 10)
    private String currency = "INR";

    /**
     * Human-readable reason if the payment failed.
     * Populated from the {@code error.description} field in the
     * Razorpay {@code payment.failed} webhook event.
     * Null for successful or pending payments.
     */
    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    /** Timestamp when this payment record was created. Immutable after insert. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    /** Timestamp of the last update to this payment record. */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    /** Automatically updates {@code updatedAt} before any database update. */
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }
}