package com.macrobalance.payment.entity;

public enum PaymentStatus {
    PENDING,    // Razorpay order created, user hasn't paid yet
    SUCCESS,    // Payment confirmed via webhook
    FAILED,     // Payment failed
    REFUNDED    // Refund processed
}