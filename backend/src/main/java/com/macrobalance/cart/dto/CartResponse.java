package com.macrobalance.cart.dto;

import java.util.List;

public record CartResponse(
        Long cartId,
        List<CartItemResponse> items
) {
}