package com.macrobalance.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity representing a customer order.
 *
 * <p>An order is created from a user's active cart during checkout.
 * It holds a snapshot of what was purchased, at what price, and
 * which address it should be delivered to.
 *
 * <p>Orders are immutable after creation — product names and prices
 * are snapshotted into {@link OrderItem} records so historical orders
 * remain accurate even if products are later updated or deleted.
 *
 * <p>Lifecycle is tracked through {@link OrderStatus} with every
 * transition recorded in {@link OrderStatusHistory} for a full audit trail.
 */
@Entity
@Getter
@Setter
@Table(name = "orders")
public class Order {

    /** Primary key, auto-incremented. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID of the user who placed this order.
     * Stored as a plain Long to avoid a bidirectional JPA
     * relationship with the User entity.
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * ID of the delivery address selected at checkout.
     * Stored as a snapshot reference — the address record may change
     * after the order is placed, but this ID links to what was selected.
     */
    @Column(name = "address_id", nullable = false)
    private Long addressId;

    /**
     * Total order amount in INR, including shipping charges.
     * Free shipping applied for orders above ₹500, otherwise ₹49.
     */
    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    /**
     * Current lifecycle status of the order.
     * Defaults to {@link OrderStatus#PENDING} on creation.
     * See {@link OrderStatus} for the full transition graph.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    /**
     * Optional delivery instructions provided by the customer at checkout.
     * Example: "Leave at door", "Call before delivery".
     */
    @Column(columnDefinition = "TEXT")
    private String notes;

    /**
     * Line items in this order — one per distinct product.
     * Cascaded: items are created and deleted with the order.
     * Loaded lazily to avoid unnecessary joins.
     */
    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<OrderItem> items = new ArrayList<>();

    /**
     * Full audit trail of every status transition this order has gone through.
     * Each entry records the old status, new status, timestamp, and who made the change.
     */
    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<OrderStatusHistory> statusHistory = new ArrayList<>();

    /** Timestamp when the order was placed. Immutable after insert. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    /** Timestamp of the last update to this order record. */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    /** Automatically updates {@code updatedAt} before any database update. */
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }

    /**
     * Adds an {@link OrderItem} to this order and sets the back-reference.
     * Use this helper instead of {@code getItems().add(item)} directly
     * to maintain bidirectional consistency.
     *
     * @param item the order item to add
     */
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    /**
     * Appends a status history record and sets the back-reference.
     * Use this helper instead of {@code getStatusHistory().add(history)}
     * to maintain bidirectional consistency.
     *
     * @param history the status history entry to append
     */
    public void addStatusHistory(OrderStatusHistory history) {
        statusHistory.add(history);
        history.setOrder(this);
    }
}