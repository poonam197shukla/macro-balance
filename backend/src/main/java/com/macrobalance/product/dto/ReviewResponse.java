package com.macrobalance.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Response payload representing a single product review.
 *
 * <p>Returned by the review listing and write endpoints.
 * The reviewer's full email is not exposed — only their display name.
 */
@Schema(description = "A product review submitted by a user")
public record ReviewResponse(

        @Schema(description = "Unique review ID", example = "12")
        Long id,

        @Schema(description = "Display name of the reviewer", example = "Rohan M.")
        String userName,

        @Schema(description = "Star rating from 1 to 5", example = "5")
        Short rating,

        @Schema(description = "Short review headline", example = "Best protein bar I've tried!")
        String title,

        @Schema(description = "Detailed review body", example = "Great taste, no chalky aftertaste.")
        String body,

        @Schema(
                description = "Whether the reviewer has a DELIVERED order containing this product. " +
                        "Set automatically by the system — cannot be set by users.",
                example = "true"
        )
        boolean isVerifiedPurchase,

        @Schema(description = "Timestamp when the review was submitted", example = "2026-04-13T15:30:00Z")
        Instant createdAt

) { }