package com.macrobalance.payment.dto;

import com.macrobalance.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        Long id,
        Long orderId,
        String razorpayOrderId,
        String razorpayPaymentId,
        PaymentStatus status,
        BigDecimal amount,
        String currency,
        String failureReason,
        Instant createdAt
) {}