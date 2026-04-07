package com.macrobalance.cart.controller;

import com.macrobalance.cart.dto.AddToCartRequest;
import com.macrobalance.cart.dto.CartResponse;
import com.macrobalance.cart.entity.CartItem;
import com.macrobalance.cart.service.CartService;
import com.macrobalance.common.dto.ApiResponse;
import com.macrobalance.security.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final JwtUtil jwtUtil;


    // Add to Cart
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Object>> addToCart(
            @RequestHeader(value = "X-Guest-Id", required = false) String guestId,
            @RequestBody @Valid AddToCartRequest request,
            Authentication authentication
    ) {

        Long userId = extractUserId(authentication);

        cartService.addToCart(
                userId,
                guestId,
                request.productId(),
                request.quantity()
        );

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Item added", null)
        );
    }

    // Get Cart
    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(
            @RequestHeader(value = "X-Guest-Id", required = false) String guestId,
            Authentication authentication
    ) {

        Long userId = extractUserId(authentication);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Cart fetched successfully", cartService.getCart(userId, guestId)));
    }

    // Extract userId from auth (simple version)
    private Long extractUserId(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        return (Long) authentication.getPrincipal();
    }
}