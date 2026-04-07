package com.macrobalance.product.dto;

import jakarta.validation.constraints.*;

public record CreateReviewRequest(

        @NotNull
        @Min(1) @Max(5)
        Short rating,

        @Size(max = 150)
        String title,

        String body
) {}