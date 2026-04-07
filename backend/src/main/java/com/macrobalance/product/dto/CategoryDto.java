package com.macrobalance.product.dto;

public record CategoryDto(
        Long id,
        String name,
        String slug,
        String description,
        boolean isActive
) {}