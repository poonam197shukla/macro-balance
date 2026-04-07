package com.macrobalance.product.dto;

import java.math.BigDecimal;

// Full detail — used on product detail page
public record ProductResponse(
        Long id,
        String name,
        String slug,
        String description,
        BigDecimal price,
        Integer stock,
        String categoryName,
        BigDecimal avgRating,
        Integer reviewCount,
        NutritionDto nutrition
) {}