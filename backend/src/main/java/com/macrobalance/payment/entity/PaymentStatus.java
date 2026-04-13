package com.macrobalance.payment.entity;

/**
 * Enum representing the lifecycle states of a payment.
 *
 * <p>Payment status transitions:
 * <pre>
 * PENDING → SUCCESS   (payment.captured webhook received and verified)
 * PENDING → FAILED    (payment.failed webhook received)
 * SUCCESS → REFUNDED  (refund processed — manual admin action for now)
 * </pre>
 *
 * <p>A corresponding {@link com.macrobalance.order.entity.OrderStatus} transition
 * is triggered on each payment status change:
 * <ul>
 *   <li>{@code PENDING → SUCCESS} triggers {@code Order: PENDING → CONFIRMED}</li>
 *   <li>{@code PENDING → FAILED} leaves the order in {@code PENDING}
 *       so the user can retry payment</li>
 * </ul>
 */
public enum PaymentStatus {

    /** Razorpay order created, user has not yet completed payment. */
    PENDING,

    /** Payment confirmed via Razorpay {@code payment.captured} webhook. */
    SUCCESS,

    /** Payment attempt failed — user may retry. */
    FAILED,

    /** Refund processed for a cancelled or returned order. */
    REFUNDED
}