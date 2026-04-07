package com.macrobalance.order.dto;

import com.macrobalance.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        OrderStatus status,
        BigDecimal totalAmount,
        Long addressId,
        String notes,
        List<OrderItemResponse> items,
        Instant createdAt
) {}