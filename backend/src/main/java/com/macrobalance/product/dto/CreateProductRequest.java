package com.macrobalance.product.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateProductRequest(

        @NotBlank
        String name,

        @NotBlank
        String slug,

        String description,

        @NotNull
        @DecimalMin("0.0")
        BigDecimal price,

        @NotNull
        @Min(0)
        Integer stock,

        @NotNull
        Long categoryId,

        @NotNull
        @Valid          // triggers validation inside NutritionDto
        NutritionDto nutrition
) {
}