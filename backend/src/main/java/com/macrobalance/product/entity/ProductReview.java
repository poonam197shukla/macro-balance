package com.macrobalance.product.entity;

import com.macrobalance.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * JPA entity representing a user's review of a product.
 *
 * <p>The unique constraint on {@code (user_id, product_id)} enforces
 * one review per user per product at the database level, complementing
 * the service-layer check.
 *
 * <p>Key design decisions:
 * <ul>
 *   <li>{@code isVerifiedPurchase} is set by the system only when the
 *       order containing this product transitions to {@code DELIVERED}.
 *       Users cannot set it themselves.</li>
 *   <li>The {@link User} relationship is a true JPA association (unlike
 *       order items which use plain Long IDs) because reviews are always
 *       displayed with the reviewer's name.</li>
 * </ul>
 *
 * <p>After any review change, {@code ReviewService} recalculates and
 * persists {@link Product#getAvgRating()} and {@link Product#getReviewCount()}.
 */
@Entity
@Getter
@Setter
@Table(
        name = "product_reviews",
        uniqueConstraints = @UniqueConstraint(
                name = "unique_user_product_review",
                columnNames = {"user_id", "product_id"}
        )
)
public class ProductReview {

    /**
     * Primary key, auto-incremented.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The product being reviewed.
     * Loaded lazily to avoid unnecessary product joins when listing reviews.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * The user who submitted this review.
     * Loaded lazily; the user's display name is resolved at the
     * service layer when building {@link com.macrobalance.product.dto.ReviewResponse}.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Star rating from 1 (lowest) to 5 (highest).
     * Used to calculate the product's {@code avgRating}.
     * Validated at the DTO layer with {@code @Min(1) @Max(5)}.
     */
    @Column(nullable = false)
    private Short rating;

    /**
     * Optional short review headline (max 150 characters).
     * Example: {@code "Best protein bar I've tried!"}.
     */
    @Column(length = 150)
    private String title;

    /**
     * Optional detailed review body.
     * Stored as TEXT to allow longer reviews.
     */
    @Column(columnDefinition = "TEXT")
    private String body;

    /**
     * Whether the reviewer purchased and received this product.
     * Set to {@code true} automatically by {@code OrderService} when an order
     * containing this product transitions to {@code DELIVERED}.
     * Never set based on user input — enforced at the service layer.
     */
    @Column(name = "is_verified_purchase", nullable = false)
    private boolean isVerifiedPurchase = false;

    /**
     * Timestamp when this review was submitted. Immutable after insert.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    /**
     * Timestamp of the last update to this review.
     */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    /**
     * Automatically updates {@code updatedAt} before any database update.
     */
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }
}