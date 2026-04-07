package com.macrobalance.product.dto;

import java.math.BigDecimal;

// Lightweight — used on shop listing page
public record ProductSummaryResponse(
        Long id,
        String name,
        String slug,
        BigDecimal price,
        String categoryName,
        BigDecimal avgRating,
        Integer reviewCount,
        // Only key macros shown on card
        BigDecimal protein,
        BigDecimal fiber,
        BigDecimal sugar
) {}