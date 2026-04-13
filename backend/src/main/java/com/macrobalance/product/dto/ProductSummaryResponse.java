package com.macrobalance.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Lightweight product summary used on the shop listing page.
 *
 * <p>Returned by {@code GET /api/products} (paginated). Contains only
 * the fields needed to render a product card — price, rating, category,
 * and the three key macros displayed on the card (protein, fiber, sugar).
 *
 * <p>Use {@code GET /api/products/{slug}} for the full detail including
 * all nutritional values.
 */
@Schema(description = "Lightweight product summary for the shop listing page")
public record ProductSummaryResponse(

        @Schema(description = "Unique product ID", example = "4")
        Long id,

        @Schema(description = "Product display name", example = "Protein Bar – Iron (Stronger Every Day)")
        String name,

        @Schema(
                description = "URL-friendly slug — use this to link to the product detail page",
                example = "protein-bar-iron"
        )
        String slug,

        @Schema(description = "Selling price in INR", example = "249.00")
        BigDecimal price,

        @Schema(description = "Category name for display on the card", example = "Protein Bars")
        String categoryName,

        @Schema(description = "Average star rating (1.0–5.0)", example = "4.5")
        BigDecimal avgRating,

        @Schema(description = "Total number of reviews", example = "23")
        Integer reviewCount,

        @Schema(description = "Protein per 100g (g) — displayed on the product card", example = "20.5")
        BigDecimal protein,

        @Schema(description = "Dietary fiber per 100g (g) — displayed on the product card", example = "4.5")
        BigDecimal fiber,

        @Schema(description = "Total sugar per 100g (g) — displayed on the product card", example = "8.0")
        BigDecimal sugar

) {}