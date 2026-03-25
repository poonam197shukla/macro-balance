package com.macrobalance.cart.dto;

public record CartItemResponse(
        Long productId,
        Integer quantity
) {
}
