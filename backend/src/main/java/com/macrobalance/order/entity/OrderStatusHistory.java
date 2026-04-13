package com.macrobalance.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * JPA entity representing a single status transition in an order's history.
 *
 * <p>Every time an order's status changes, a new record is appended here.
 * This provides a complete, immutable audit trail of how an order progressed
 * from placement through to delivery or cancellation.
 *
 * <p>The first entry in the history will always have {@code oldStatus = null}
 * since the order starts in {@link OrderStatus#PENDING} with no prior state.
 */
@Entity
@Getter
@Setter
@Table(name = "order_status_history")
public class OrderStatusHistory {

    /** Primary key, auto-incremented. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The order this history entry belongs to.
     * Loaded lazily to avoid unnecessary joins.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * The status the order was in before this transition.
     * Null for the first history entry when the order is created.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "old_status")
    private OrderStatus oldStatus;

    /**
     * The status the order transitioned into.
     * Never null — every history entry must record the resulting state.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private OrderStatus newStatus;

    /** Timestamp when this status transition occurred. */
    @Column(name = "changed_at", nullable = false)
    private Instant changedAt = Instant.now();

    /**
     * Identifier of who triggered this status change.
     * Examples:
     * <ul>
     *   <li>{@code "SYSTEM"} — automatic transition on order creation</li>
     *   <li>{@code "RAZORPAY_WEBHOOK"} — payment confirmed by webhook</li>
     *   <li>{@code "admin@macrobalance.com"} — manual update by an admin</li>
     *   <li>{@code "priya@example.com"} — cancelled by the user</li>
     * </ul>
     */
    @Column(name = "changed_by")
    private String changedBy;
}