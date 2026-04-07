package com.macrobalance.order.entity;

public enum OrderStatus {
    PENDING,        // order placed, payment not confirmed
    CONFIRMED,      // payment confirmed
    PROCESSING,     // being packed
    SHIPPED,        // out for delivery
    DELIVERED,      // delivered to customer
    CANCELLED,      // cancelled before shipping
    REFUNDED        // refund processed
}