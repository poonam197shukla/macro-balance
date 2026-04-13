package com.macrobalance.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Response payload representing a single line item within an order.
 *
 * <p>All values are snapshots captured at the time of purchase.
 * The product name and price reflect what the customer actually bought,
 * regardless of any subsequent changes to the product catalogue.
 */
@Schema(description = "A single line item within an order")
public record OrderItemResponse(

        @Schema(description = "ID of the product", example = "4")
        Long productId,

        @Schema(
                description = "Product name snapshot at time of purchase. " +
                        "Does not change if the product is later renamed.",
                example = "Protein Bar – Iron (Stronger Every Day)"
        )
        String productName,

        @Schema(description = "Number of units ordered", example = "2")
        Integer quantity,

        @Schema(
                description = "Unit price at time of purchase in INR. " +
                        "Does not change if the product price is later updated.",
                example = "249.00"
        )
        BigDecimal priceAtPurchase,

        @Schema(
                description = "Line total — quantity × priceAtPurchase",
                example = "498.00"
        )
        BigDecimal lineTotal

) {}