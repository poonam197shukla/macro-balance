package com.macrobalance.product.entity;

import com.macrobalance.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity representing a product in the MacroBalance catalogue.
 *
 * <p>Products are the core domain object of the platform. Each product has:
 * <ul>
 *   <li>A unique {@code slug} for SEO-friendly URLs and public API routing</li>
 *   <li>A {@link ProductNutrition} record (1:1) storing per-100g nutritional values</li>
 *   <li>Denormalised {@code avgRating} and {@code reviewCount} fields updated
 *       on every review change to avoid expensive aggregation queries</li>
 * </ul>
 *
 * <p>Products support soft deletion via {@code isActive}. Inactive products
 * are hidden from public endpoints but not deleted, preserving order history
 * integrity since {@link com.macrobalance.order.entity.OrderItem} references
 * the product ID.
 *
 * <p>Extends {@link BaseEntity} for {@code id}, audit timestamps,
 * {@code createdBy}, {@code updatedBy}, and optimistic locking via {@code version}.
 */
@Entity
@Getter
@Setter
@Table(name = "products")
public class Product extends BaseEntity {

    /**
     * Display name of the product shown in the UI.
     * Example: {@code "Protein Bar – Iron (Stronger Every Day)"}.
     */
    @Column(nullable = false, length = 255)
    private String name;

    /**
     * URL-friendly identifier used for public API routing.
     * Must be globally unique, lowercase, and hyphen-separated.
     * Example: {@code "protein-bar-iron"}.
     */
    @Column(nullable = false, unique = true, length = 255)
    private String slug;

    /**
     * Full product description shown on the detail page.
     * Stored as TEXT to support multi-paragraph content.
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Selling price in INR. Must be non-negative.
     * Stored with 10 digits precision and 2 decimal places.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * Available stock units. Must be non-negative.
     * Decremented at checkout, restored on order cancellation.
     * Stock availability is checked at checkout, not when adding to cart.
     */
    @Column(nullable = false)
    private Integer stock;

    /**
     * Whether this product is visible in the public catalogue.
     * Defaults to {@code true}. Set to {@code false} to soft-delete.
     */
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    /**
     * Denormalised average star rating across all reviews.
     * Recalculated by {@code ReviewService} on every create, update,
     * or delete review operation. Stored to avoid aggregation queries
     * on the listing page.
     */
    @Column(name = "avg_rating", precision = 2, scale = 1)
    private BigDecimal avgRating = BigDecimal.ZERO;

    /**
     * Denormalised total number of reviews for this product.
     * Recalculated alongside {@code avgRating} on every review change.
     */
    @Column(name = "review_count")
    private Integer reviewCount = 0;

    // ── Relationships ──────────────────────────────────────────────────────────

    /**
     * The category this product belongs to.
     * Loaded lazily to avoid unnecessary joins on product listing queries.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /**
     * Nutritional information for this product (1:1).
     * Cascaded — created and deleted atomically with the product.
     * Stored in a separate table ({@code product_nutrition}) to allow
     * clean extensibility for future nutritional fields via migrations.
     */
    @OneToOne(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private ProductNutrition nutrition;

    /**
     * All reviews for this product.
     * Cascaded — reviews are deleted when the product is deleted.
     * Loaded lazily; accessed only when needed (review listing endpoint).
     */
    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<ProductReview> reviews = new ArrayList<>();

    // ── Helper methods ─────────────────────────────────────────────────────────

    /**
     * Sets the nutritional information for this product and maintains
     * the bidirectional relationship by setting the back-reference on
     * the {@link ProductNutrition} entity.
     *
     * <p>Use this helper instead of {@code setNutrition()} + manually
     * setting {@code nutrition.setProduct(this)}.
     *
     * @param nutrition the nutritional data to associate with this product
     */
    public void setNutrition(ProductNutrition nutrition) {
        this.nutrition = nutrition;
        nutrition.setProduct(this);
    }
}