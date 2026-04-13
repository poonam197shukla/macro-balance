package com.macrobalance.order.dto;

import com.macrobalance.order.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Lightweight order summary used in the order history list.
 *
 * <p>Returned by {@code GET /api/orders} (paginated).
 * Does not include individual line items — use
 * {@code GET /api/orders/{id}} for full item details.
 */
@Schema(description = "Lightweight order summary for listing in order history")
public record OrderSummaryResponse(

        @Schema(description = "Unique order identifier", example = "42")
        Long id,

        @Schema(description = "Current order status", example = "SHIPPED")
        OrderStatus status,

        @Schema(description = "Total order amount in INR", example = "547.00")
        BigDecimal totalAmount,

        @Schema(description = "Number of distinct products in the order", example = "3")
        int itemCount,

        @Schema(description = "Timestamp when the order was placed", example = "2026-04-13T15:30:00Z")
        Instant createdAt

) {}