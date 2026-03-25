package com.macrobalance.cart.controller;

import com.macrobalance.cart.entity.CartItem;
import com.macrobalance.cart.service.CartService;
import com.macrobalance.security.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final JwtUtil jwtUtil;


    // 🔹 Add to Cart
    @PostMapping("/add")
    public void addToCart(
            @RequestHeader(value = "X-Guest-Id", required = false) String guestId,
            @RequestParam Long productId,
            @RequestParam int quantity,
            HttpServletRequest request) {

        Long userId = extractUserId(request);

        cartService.addToCart(userId, guestId, productId, quantity);
    }

    // 🔹 Get Cart
    @GetMapping
    public List<CartItem> getCart(
            @RequestHeader(value = "X-Guest-Id", required = false) String guestId,
            HttpServletRequest request) {

        Long userId = extractUserId(request);

        return cartService.getCart(userId, guestId);
    }

    // 🔥 Extract userId from auth (simple version)
    private Long extractUserId(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtil.extractUserId(token);
        }

        return null;
    }
}