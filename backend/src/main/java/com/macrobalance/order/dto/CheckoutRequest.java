package com.macrobalance.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload for placing an order from the active cart.
 *
 * <p>The user must have an active cart with at least one item,
 * and the specified address must belong to the authenticated user.
 */
@Schema(description = "Request payload for placing an order from the active cart")
public record CheckoutRequest(

        @Schema(
                description = "ID of the delivery address to ship to. " +
                        "Must belong to the authenticated user.",
                example = "1"
        )
        @NotNull
        Long addressId,

        @Schema(
                description = "Optional delivery instructions for the courier",
                example = "Please leave at the door"
        )
        String notes

) {}