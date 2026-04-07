package com.macrobalance.payment.dto;

import java.math.BigDecimal;

// Returned to frontend so it can open the Razorpay modal
public record InitiatePaymentResponse(
        String razorpayOrderId,
        BigDecimal amount,
        String currency,
        String keyId          // frontend needs this to init Razorpay SDK
) {}