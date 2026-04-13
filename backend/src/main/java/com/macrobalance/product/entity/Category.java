package com.macrobalance.product.entity;

import com.macrobalance.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity representing a product category.
 *
 * <p>Categories organise the MacroBalance product catalogue into
 * logical groups (e.g. Protein Bars, Date Bites, Roasted Makhana).
 * Each category has a URL-friendly {@code slug} used as a query
 * parameter for product filtering.
 *
 * <p>Categories support soft deletion via {@code isActive} — deactivated
 * categories are hidden from the public catalogue but not permanently removed.
 *
 * <p>Extends {@link BaseEntity} for {@code id}, audit timestamps,
 * {@code createdBy}, {@code updatedBy}, and optimistic locking via {@code version}.
 */
@Entity
@Getter
@Setter
@Table(name = "categories")
public class Category extends BaseEntity {

    /**
     * Human-readable display name shown in the UI.
     * Must be unique across all categories.
     * Example: {@code "Protein Bars"}.
     */
    @Column(nullable = false, unique = true, length = 150)
    private String name;

    /**
     * URL-friendly identifier used as a query parameter for product filtering.
     * Must be unique, lowercase, and hyphen-separated.
     * Example: {@code "protein-bars"}.
     */
    @Column(nullable = false, unique = true, length = 150)
    private String slug;

    /**
     * Optional longer description of the category shown on category pages.
     * Stored as TEXT to allow multi-paragraph descriptions.
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Whether this category is visible in the public catalogue.
     * Defaults to {@code true}. Set to {@code false} to soft-delete.
     * Products belonging to an inactive category are also hidden.
     */
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    /**
     * Products belonging to this category.
     * Loaded lazily — not included in category listing responses.
     * Navigated only when explicitly needed (e.g. admin bulk operations).
     */
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();
}