package com.macrobalance.product.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record NutritionDto(

        @NotNull
        @Min(1)
        Integer servingSizeG,

        @NotNull @DecimalMin("0.0") BigDecimal calories,
        @NotNull @DecimalMin("0.0") BigDecimal protein,
        @NotNull @DecimalMin("0.0") BigDecimal carbs,
        @NotNull @DecimalMin("0.0") BigDecimal fiber,
        @NotNull @DecimalMin("0.0") BigDecimal sugar,
        @NotNull @DecimalMin("0.0") BigDecimal fat
) {}