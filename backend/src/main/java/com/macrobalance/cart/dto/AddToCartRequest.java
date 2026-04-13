package com.macrobalance.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload for adding a product to the cart.
 *
 * <p>If the product is already in the cart, the specified
 * quantity is added to the existing quantity rather than
 * replacing it.
 */
@Schema(description = "Request payload for adding a product to the cart")
public record AddToCartRequest(

        @Schema(
                description = "ID of the product to add",
                example = "4"
        )
        @NotNull
        Long productId,

        @Schema(
                description = "Number of units to add. Must be at least 1.",
                example = "2"
        )
        @Min(1)
        int quantity

) {}