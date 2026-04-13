package com.macrobalance.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload for initiating a Razorpay payment against an existing order.
 *
 * <p>The order must be in {@code PENDING} status and must belong to
 * the authenticated user. Call {@code POST /api/orders/checkout} first
 * to create the order before calling the payment initiation endpoint.
 */
@Schema(description = "Request payload for initiating a Razorpay payment")
public record InitiatePaymentRequest(

        @Schema(
                description = "ID of the MacroBalance order to pay for. " +
                        "Order must be in PENDING status and belong to the authenticated user.",
                example = "42"
        )
        @NotNull
        Long orderId

) {}