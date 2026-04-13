package com.macrobalance.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Response payload representing the full cart state.
 *
 * <p>Returned by {@code GET /api/cart} and after any cart mutation.
 * The {@code cartId} can be used by the frontend to track the
 * session cart across requests.
 */
@Schema(description = "Current state of the shopping cart")
public record CartResponse(

        @Schema(description = "Unique identifier of the cart", example = "7")
        Long cartId,

        @Schema(description = "List of items currently in the cart")
        List<CartItemResponse> items

) {}