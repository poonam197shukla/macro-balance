package com.macrobalance.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * Request payload for creating or updating a product.
 *
 * <p>Used by both {@code POST /api/products/admin} and
 * {@code PUT /api/products/admin/{id}}.
 * Nutritional data is required and validated recursively via {@code @Valid}.
 */
@Schema(description = "Request payload for creating or updating a product")
public record CreateProductRequest(

        @Schema(description = "Product display name", example = "Protein Bar – Iron (Stronger Every Day)")
        @NotBlank
        String name,

        @Schema(
                description = "URL-friendly slug — lowercase, hyphen-separated, globally unique",
                example = "protein-bar-iron"
        )
        @NotBlank
        String slug,

        @Schema(
                description = "Full product description shown on the detail page",
                example = "A high-protein bar made with whey isolate and oats."
        )
        String description,

        @Schema(description = "Selling price in INR", example = "249.00")
        @NotNull
        @DecimalMin("0.0")
        BigDecimal price,

        @Schema(description = "Available stock units", example = "150")
        @NotNull
        @Min(0)
        Integer stock,

        @Schema(description = "ID of the category this product belongs to", example = "2")
        @NotNull
        Long categoryId,

        @Schema(description = "Nutritional information per 100g serving")
        @NotNull
        @Valid
        NutritionDto nutrition

) {}