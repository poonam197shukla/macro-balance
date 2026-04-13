package com.macrobalance.product.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * JPA entity storing the nutritional information for a product.
 *
 * <p>Maintained as a separate table from {@link Product} rather than
 * additional columns, for two reasons:
 * <ul>
 *   <li>Cleaner separation of concerns between product metadata and nutrition</li>
 *   <li>New nutritional fields (e.g. sodium, vitamins) can be added via
 *       Flyway migrations to this table without touching the products table</li>
 * </ul>
 *
 * <p>All values represent the nutritional content per {@code servingSizeG} grams
 * (default 100g). Stored with 1 decimal place precision.
 *
 * <p>Does not extend {@link com.macrobalance.common.entity.BaseEntity} —
 * nutrition has no independent audit requirement; it lives and dies with
 * its parent {@link Product} via {@code CascadeType.ALL}.
 */
@Entity
@Getter
@Setter
@Table(name = "product_nutrition")
public class ProductNutrition {

    /**
     * Primary key, auto-incremented.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The product this nutritional data belongs to.
     * The unique constraint on the join column ensures strict 1:1 mapping.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    /**
     * The serving size in grams that all nutritional values below refer to.
     * Defaults to 100g as per standard nutritional labelling in India.
     */
    @Column(name = "serving_size_g", nullable = false)
    private Integer servingSizeG = 100;

    /**
     * Calories per serving in kcal.
     */
    @Column(precision = 6, scale = 1)
    private BigDecimal calories = BigDecimal.ZERO;

    /**
     * Protein content per serving in grams.
     */
    @Column(precision = 5, scale = 1)
    private BigDecimal protein = BigDecimal.ZERO;

    /**
     * Total carbohydrates per serving in grams.
     */
    @Column(precision = 5, scale = 1)
    private BigDecimal carbs = BigDecimal.ZERO;

    /**
     * Dietary fiber per serving in grams.
     */
    @Column(precision = 5, scale = 1)
    private BigDecimal fiber = BigDecimal.ZERO;

    /**
     * Total sugar per serving in grams.
     * Used in the nutritional filter ({@code maxSugar}) on the listing page.
     */
    @Column(precision = 5, scale = 1)
    private BigDecimal sugar = BigDecimal.ZERO;

    /**
     * Total fat per serving in grams.
     */
    @Column(precision = 5, scale = 1)
    private BigDecimal fat = BigDecimal.ZERO;

    /**
     * Timestamp when this nutrition record was created. Immutable after insert.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    /**
     * Timestamp of the last update to this nutrition record.
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