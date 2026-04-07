package com.macrobalance.product.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "product_nutrition")
public class ProductNutrition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // No BaseEntity here — nutrition has no auditing need,
    // it lives and dies with the product

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @Column(name = "serving_size_g", nullable = false)
    private Integer servingSizeG = 100;

    @Column(precision = 6, scale = 1)
    private BigDecimal calories = BigDecimal.ZERO;

    @Column(precision = 5, scale = 1)
    private BigDecimal protein = BigDecimal.ZERO;

    @Column(precision = 5, scale = 1)
    private BigDecimal carbs = BigDecimal.ZERO;

    @Column(precision = 5, scale = 1)
    private BigDecimal fiber = BigDecimal.ZERO;

    @Column(precision = 5, scale = 1)
    private BigDecimal sugar = BigDecimal.ZERO;

    @Column(precision = 5, scale = 1)
    private BigDecimal fat = BigDecimal.ZERO;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }
}