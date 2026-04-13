package com.macrobalance.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

/**
 * Request payload for submitting or updating a product review.
 *
 * <p>Each user may submit one review per product. The {@code isVerifiedPurchase}
 * flag is set by the system — users cannot set it through this request.
 */
@Schema(description = "Request payload for submitting or updating a product review")
public record CreateReviewRequest(

        @Schema(
                description = "Star rating from 1 (worst) to 5 (best)",
                example = "5",
                minimum = "1",
                maximum = "5"
        )
        @NotNull
        @Min(1) @Max(5)
        Short rating,

        @Schema(
                description = "Short review headline (optional, max 150 characters)",
                example = "Best protein bar I've tried!"
        )
        @Size(max = 150)
        String title,

        @Schema(
                description = "Detailed review body (optional)",
                example = "Great taste, high protein, and doesn't taste chalky at all."
        )
        String body

) {}