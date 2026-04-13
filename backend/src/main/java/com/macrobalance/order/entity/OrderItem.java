package com.macrobalance.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * JPA entity representing a single line item within an order.
 *
 * <p>Order items are snapshots — the product name and price are
 * captured at the moment of purchase and never updated afterward.
 * This ensures order history remains accurate even if a product is
 * renamed, repriced, or deleted from the catalogue.
 *
 * <p>Stock deduction happens during checkout when order items are created.
 * If the order is later cancelled, stock is restored via the service layer.
 */
@Entity
@Getter
@Setter
@Table(name = "order_items")
public class OrderItem {

    /** Primary key, auto-incremented. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The parent order this item belongs to.
     * Loaded lazily to avoid unnecessary joins.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * ID of the product that was purchased.
     * Kept as a plain Long rather than a {@code @ManyToOne} so that
     * product deletion does not break order history.
     */
    @Column(name = "product_id", nullable = false)
    private Long productId;

    /**
     * Product name at the time of purchase.
     * Snapshot — never updated, even if the product is renamed later.
     */
    @Column(name = "product_name", nullable = false)
    private String productName;

    /** Number of units purchased. Always at least 1. */
    @Column(nullable = false)
    private Integer quantity;

    /**
     * Unit price of the product at the time of purchase in INR.
     * Snapshot — never updated, even if the product price changes later.
     * Line total = {@code priceAtPurchase × quantity}.
     */
    @Column(name = "price_at_purchase", nullable = false)
    private BigDecimal priceAtPurchase;

    /** Timestamp when this order item was created. Immutable after insert. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    /** Timestamp of the last update to this record. */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();
}