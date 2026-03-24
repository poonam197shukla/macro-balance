package com.macrobalance.cart.controller;

import com.macrobalance.cart.entity.CartItem;
import com.macrobalance.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // 🔹 Add to Cart
    @PostMapping("/add")
    public void addToCart(
            @RequestHeader(value = "X-Guest-Id", required = false) String guestId,
            @RequestParam Long productId,
            @RequestParam int quantity,
            Authentication authentication
    ) {

        Long userId = extractUserId(authentication);

        cartService.addToCart(userId, guestId, productId, quantity);
    }

    // 🔹 Get Cart
    @GetMapping
    public List<CartItem> getCart(
            @RequestHeader(value = "X-Guest-Id", required = false) String guestId,
            Authentication authentication
    ) {

        Long userId = extractUserId(authentication);

        return cartService.getCart(userId, guestId);
    }

    // 🔥 Extract userId from auth (simple version)
    private Long extractUserId(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        // for now using email as principal
        // later you should fetch userId from DB or JWT
        return null; // TODO: implement properly
    }
}