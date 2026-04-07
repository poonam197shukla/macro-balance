package com.macrobalance.product.dto;

import java.time.Instant;

public record ReviewResponse(
        Long id,
        String userName,
        Short rating,
        String title,
        String body,
        boolean isVerifiedPurchase,
        Instant createdAt
) {}