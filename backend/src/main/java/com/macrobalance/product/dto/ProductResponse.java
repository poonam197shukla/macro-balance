package com.macrobalance.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Full product detail response used on the product detail page.
 *
 * <p>Returned by {@code GET /api/products/{slug}} and admin write endpoints.
 * Includes complete nutritional data, unlike {@link ProductSummaryResponse}
 * which only contains the key macros.
 */
@Schema(description = "Full product detail including all nutritional values")
public record ProductResponse(

        @Schema(description = "Unique product ID", example = "4")
        Long id,

        @Schema(description = "Product display name", example = "Protein Bar – Iron (Stronger Every Day)")
        String name,

        @Schema(description = "URL-friendly slug", example = "protein-bar-iron")
        String slug,

        @Schema(description = "Full product description", example = "A high-protein bar made with whey isolate.")
        String description,

        @Schema(description = "Selling price in INR", example = "249.00")
        BigDecimal price,

        @Schema(description = "Available stock units", example = "150")
        Integer stock,

        @Schema(description = "Name of the category this product belongs to", example = "Protein Bars")
        String categoryName,

        @Schema(description = "Average star rating from all reviews (1.0–5.0)", example = "4.5")
        BigDecimal avgRating,

        @Schema(description = "Total number of reviews", example = "23")
        Integer reviewCount,

        @Schema(description = "Complete nutritional information per 100g serving")
        NutritionDto nutrition

) {}