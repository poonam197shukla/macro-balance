package com.macrobalance.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for creating or updating a product category.
 *
 * <p>Both {@code name} and {@code slug} must be globally unique.
 * The slug should be lowercase and hyphen-separated to work correctly
 * as a URL query parameter.
 */
@Schema(description = "Request payload for creating or updating a product category")
public record CreateCategoryRequest(

        @Schema(description = "Display name of the category", example = "Protein Bars")
        @NotBlank
        @Size(max = 150)
        String name,

        @Schema(
                description = "URL-friendly slug — lowercase, hyphen-separated, globally unique",
                example = "protein-bars"
        )
        @NotBlank
        @Size(max = 150)
        String slug,

        @Schema(
                description = "Optional description shown on the category page",
                example = "High-protein snack bars for post-workout recovery"
        )
        String description

) {}