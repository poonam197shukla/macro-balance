package com.macrobalance.product.dto;

import java.math.BigDecimal;

public record NutritionDto(
        Integer servingSizeG,
        BigDecimal calories,
        BigDecimal protein,
        BigDecimal carbs,
        BigDecimal fiber,
        BigDecimal sugar,
        BigDecimal fat
) {}