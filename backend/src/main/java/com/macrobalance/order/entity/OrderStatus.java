package com.macrobalance.order.entity;

/**
 * Enum representing the lifecycle stages of a customer order.
 *
 * <p>Valid transitions are enforced by {@code OrderService.validateStatusTransition()}:
 * <pre>
 * PENDING    → CONFIRMED  (payment received via Razorpay webhook)
 * PENDING    → CANCELLED  (cancelled before payment)
 * CONFIRMED  → PROCESSING (admin begins packing)
 * CONFIRMED  → CANCELLED  (cancelled after payment but before packing)
 * PROCESSING → SHIPPED    (handed to courier)
 * SHIPPED    → DELIVERED  (delivered to customer)
 * </pre>
 *
 * <p>Side effects on specific transitions:
 * <ul>
 *   <li>{@code → DELIVERED} — marks verified purchase on product reviews</li>
 *   <li>{@code → CANCELLED} — restores stock for all order items</li>
 * </ul>
 */
public enum OrderStatus {

    /** Order placed, awaiting payment confirmation from Razorpay. */
    PENDING,

    /** Payment confirmed via Razorpay webhook. Ready for packing. */
    CONFIRMED,

    /** Order is being packed in the warehouse. */
    PROCESSING,

    /** Order handed to courier, out for delivery. */
    SHIPPED,

    /** Order successfully delivered to the customer. */
    DELIVERED,

    /** Order cancelled. Stock restored if cancelled after confirmation. */
    CANCELLED,

    /** Refund processed for a cancelled or returned order. */
    REFUNDED
}