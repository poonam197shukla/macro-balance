package com.macrobalance.payment.dto;

import jakarta.validation.constraints.NotNull;

public record InitiatePaymentRequest(
        @NotNull
        Long orderId
) {}