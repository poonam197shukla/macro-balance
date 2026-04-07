package com.macrobalance.product.dto;

import java.math.BigDecimal;

// Query params for GET /api/products
public record ProductFilterRequest(
        String categorySlug,
        BigDecimal minProtein,
        BigDecimal maxSugar,
        BigDecimal minFiber,
        BigDecimal maxCalories,
        BigDecimal maxPrice,
        String keyword
) {}