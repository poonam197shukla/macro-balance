package com.macrobalance.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response payload representing a single item in the cart.
 *
 * <p>Returns only the product ID and quantity. To display product
 * name, price, and image on the cart UI, the frontend should
 * enrich this using {@code GET /api/products/{slug}} or maintain
 * a local product cache.
 */
@Schema(description = "A single item in the shopping cart")
public record CartItemResponse(

        @Schema(description = "ID of the product", example = "4")
        Long productId,

        @Schema(description = "Number of units in the cart", example = "2")
        Integer quantity

) {}