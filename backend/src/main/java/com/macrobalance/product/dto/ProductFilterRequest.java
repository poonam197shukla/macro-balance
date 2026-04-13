package com.macrobalance.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Query parameter object for filtering the product listing.
 *
 * <p>Bound from request parameters via {@code @ModelAttribute} in
 * {@link ProductController#getProducts}. All fields are optional —
 * omitting a field means that filter is not applied.
 *
 * <p>Nutritional filters compare against per-100g values stored in
 * {@link com.macrobalance.product.entity.ProductNutrition}.
 * Implemented using JPA Specifications for dynamic query composition.
 */
@Schema(description = "Query parameters for filtering the product listing. All fields are optional.")
public record ProductFilterRequest(

        @Schema(
                description = "Filter by category slug — returns only products in that category",
                example = "protein-bars"
        )
        String categorySlug,

        @Schema(description = "Minimum protein per 100g (g)", example = "15.0")
        BigDecimal minProtein,

        @Schema(description = "Maximum sugar per 100g (g)", example = "10.0")
        BigDecimal maxSugar,

        @Schema(description = "Minimum dietary fiber per 100g (g)", example = "3.0")
        BigDecimal minFiber,

        @Schema(description = "Maximum calories per 100g (kcal)", example = "400.0")
        BigDecimal maxCalories,

        @Schema(description = "Maximum price in INR", example = "300.00")
        BigDecimal maxPrice,

        @Schema(
                description = "Full-text keyword search against product name and description",
                example = "protein"
        )
        String keyword

) {}