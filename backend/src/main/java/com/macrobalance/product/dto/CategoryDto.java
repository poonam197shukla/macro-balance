package com.macrobalance.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response payload representing a product category.
 * Used for both list and detail responses.
 */
@Schema(description = "Product category")
public record CategoryDto(

        @Schema(description = "Unique category ID", example = "2")
        Long id,

        @Schema(description = "Display name of the category", example = "Protein Bars")
        String name,

        @Schema(
                description = "URL-friendly identifier used in product filtering. " +
                        "Pass as categorySlug query param to GET /api/products.",
                example = "protein-bars"
        )
        String slug,

        @Schema(description = "Optional description of the category", example = "High-protein snack bars")
        String description,

        @Schema(description = "Whether the category is visible in the public catalogue", example = "true")
        boolean isActive

) {}