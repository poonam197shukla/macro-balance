package com.macrobalance.payment.dto;

import com.macrobalance.payment.entity.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Response payload representing the current state of a payment.
 *
 * <p>Returned by {@code GET /api/payments/{orderId}}.
 * Razorpay IDs are populated progressively — {@code razorpayOrderId}
 * is set on initiation, {@code razorpayPaymentId} is set after the
 * webhook confirms a successful payment.
 */
@Schema(description = "Payment record for a MacroBalance order")
public record PaymentResponse(

        @Schema(description = "Internal payment record ID", example = "7")
        Long id,

        @Schema(description = "MacroBalance order ID this payment belongs to", example = "42")
        Long orderId,

        @Schema(
                description = "Razorpay order ID created during payment initiation",
                example = "order_QbXk9mNpLrTzW1"
        )
        String razorpayOrderId,

        @Schema(
                description = "Razorpay payment ID received via webhook after successful payment. " +
                        "Null if payment has not yet been completed.",
                example = "pay_QbXkAjM9LrTzW2"
        )
        String razorpayPaymentId,

        @Schema(
                description = "Current payment status",
                example = "SUCCESS",
                allowableValues = {"PENDING", "SUCCESS", "FAILED", "REFUNDED"}
        )
        PaymentStatus status,

        @Schema(description = "Payment amount in INR", example = "547.00")
        BigDecimal amount,

        @Schema(description = "Currency code", example = "INR")
        String currency,

        @Schema(
                description = "Reason for payment failure, if applicable. Null for successful payments.",
                example = "Insufficient funds"
        )
        String failureReason,

        @Schema(description = "Timestamp when the payment record was created", example = "2026-04-13T15:30:00Z")
        Instant createdAt

) {}