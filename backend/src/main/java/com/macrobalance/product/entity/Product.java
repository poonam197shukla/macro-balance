package com.macrobalance.product.entity;

import com.macrobalance.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "products")
public class Product extends BaseEntity {

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "avg_rating", precision = 2, scale = 1)
    private BigDecimal avgRating = BigDecimal.ZERO;

    @Column(name = "review_count")
    private Integer reviewCount = 0;

    // ── Relationships ──────────────────────────────────────

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // CascadeType.ALL + orphanRemoval → nutrition is created/deleted with product
    @OneToOne(mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private ProductNutrition nutrition;

    @OneToMany(mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<ProductReview> reviews = new ArrayList<>();

    // ── Helper methods ─────────────────────────────────────

    public void setNutrition(ProductNutrition nutrition) {
        this.nutrition = nutrition;
        nutrition.setProduct(this);
    }
}