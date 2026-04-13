package com.macrobalance.order.dto;

import com.macrobalance.order.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Full order detail response including all line items.
 *
 * <p>Returned by {@code POST /api/orders/checkout} and
 * {@code GET /api/orders/{id}}. All monetary values are in INR.
 */
@Schema(description = "Full order detail including line items and totals")
public record OrderResponse(

        @Schema(description = "Unique order identifier", example = "42")
        Long id,

        @Schema(
                description = "Current order status",
                example = "CONFIRMED",
                allowableValues = {
                        "PENDING", "CONFIRMED", "PROCESSING",
                        "SHIPPED", "DELIVERED", "CANCELLED", "REFUNDED"
                }
        )
        OrderStatus status,

        @Schema(
                description = "Total order amount in INR including shipping",
                example = "547.00"
        )
        BigDecimal totalAmount,

        @Schema(
                description = "ID of the delivery address used for this order",
                example = "1"
        )
        Long addressId,

        @Schema(
                description = "Optional delivery notes provided at checkout",
                example = "Please leave at the door"
        )
        String notes,

        @Schema(description = "List of products ordered with quantities and prices")
        List<OrderItemResponse> items,

        @Schema(description = "Timestamp when the order was placed", example = "2026-04-13T15:30:00Z")
        Instant createdAt

) {}