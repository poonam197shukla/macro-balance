package com.macrobalance.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * Nutritional information for a product, expressed per 100g serving.
 *
 * <p>Used as a nested object inside {@link CreateProductRequest} and
 * returned inside {@link ProductResponse}. All values must be
 * non-negative. Values are stored with one decimal place precision.
 */
@Schema(description = "Nutritional values per 100g serving")
public record NutritionDto(

        @Schema(description = "Serving size in grams", example = "100")
        @NotNull
        @Min(1)
        Integer servingSizeG,

        @Schema(description = "Calories per serving (kcal)", example = "380.0")
        @NotNull @DecimalMin("0.0") BigDecimal calories,

        @Schema(description = "Protein per serving (g)", example = "20.5")
        @NotNull @DecimalMin("0.0") BigDecimal protein,

        @Schema(description = "Total carbohydrates per serving (g)", example = "42.0")
        @NotNull @DecimalMin("0.0") BigDecimal carbs,

        @Schema(description = "Dietary fiber per serving (g)", example = "4.5")
        @NotNull @DecimalMin("0.0") BigDecimal fiber,

        @Schema(description = "Total sugar per serving (g)", example = "8.0")
        @NotNull @DecimalMin("0.0") BigDecimal sugar,

        @Schema(description = "Total fat per serving (g)", example = "12.0")
        @NotNull @DecimalMin("0.0") BigDecimal fat

) {}