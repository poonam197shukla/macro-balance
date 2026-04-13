package com.macrobalance.cart.controller;

import com.macrobalance.cart.dto.AddToCartRequest;
import com.macrobalance.cart.dto.CartResponse;
import com.macrobalance.cart.service.CartService;
import com.macrobalance.common.dto.ApiResponse;
import com.macrobalance.security.jwt.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing the shopping cart.
 *
 * <p>Supports both guest and authenticated users:
 * <ul>
 *   <li><strong>Guest users</strong> — identified by the {@code X-Guest-Id}
 *       request header. The frontend generates a UUID and stores it in
 *       localStorage, sending it with every cart request.</li>
 *   <li><strong>Authenticated users</strong> — identified via JWT.
 *       On login, the guest cart is automatically merged into the
 *       user's cart.</li>
 * </ul>
 *
 * <p>Cart endpoints are public — no JWT is required to add items or
 * view the cart. Authentication is optional and enhances behaviour
 * when present.
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Shopping cart management for both guest and authenticated users")
public class CartController {

    private final CartService cartService;
    private final JwtUtil jwtUtil;

    /**
     * Adds a product to the cart, or increments its quantity if already present.
     *
     * <p>At least one of {@code X-Guest-Id} header or a valid JWT must be
     * provided so the cart can be identified. If both are provided, the
     * JWT takes precedence and the user's cart is used.
     *
     * @param guestId        optional guest identifier from the {@code X-Guest-Id} header
     * @param request        product ID and quantity to add
     * @param authentication optional JWT authentication for logged-in users
     * @return success confirmation
     */
    @Operation(
            summary = "Add item to cart",
            description = "Adds a product to the cart. If the product is already in the cart, " +
                    "the quantity is incremented. Works for both guest and authenticated users. " +
                    "Pass 'X-Guest-Id' header for guest carts."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Item added to cart"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Validation failed — invalid product ID or quantity")
    })
    @PostMapping("/items")
    public ResponseEntity<ApiResponse<Object>> addToCart(
            @Parameter(description = "Guest cart identifier — required if user is not authenticated")
            @RequestHeader(value = "X-Guest-Id", required = false) String guestId,
            @RequestBody @Valid AddToCartRequest request,
            Authentication authentication
    ) {
        Long userId = extractUserId(authentication);
        cartService.addToCart(userId, guestId, request.productId(), request.quantity());
        return ResponseEntity.ok(new ApiResponse<>(true, "Item added", null));
    }

    /**
     * Retrieves the current cart contents.
     *
     * <p>If no cart exists for the given guest or user, an empty cart
     * is created and returned. Returns product IDs and quantities —
     * the frontend is responsible for enriching with product details
     * if needed, or use the product detail endpoint for each item.
     *
     * @param guestId        optional guest identifier from the {@code X-Guest-Id} header
     * @param authentication optional JWT authentication for logged-in users
     * @return cart ID and list of items with product IDs and quantities
     */
    @Operation(
            summary = "Get cart",
            description = "Returns the current cart contents for the guest or authenticated user. " +
                    "Creates an empty cart if none exists. " +
                    "Pass 'X-Guest-Id' header for guest carts."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Cart fetched successfully")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(
            @Parameter(description = "Guest cart identifier — required if user is not authenticated")
            @RequestHeader(value = "X-Guest-Id", required = false) String guestId,
            Authentication authentication
    ) {
        Long userId = extractUserId(authentication);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Cart fetched successfully",
                cartService.getCart(userId, guestId)
        ));
    }

    /**
     * Extracts the user ID from the JWT authentication context.
     *
     * <p>Returns {@code null} for unauthenticated (guest) requests,
     * allowing the cart service to fall back to guest cart lookup.
     *
     * @param authentication the Spring Security authentication object, may be null
     * @return the authenticated user's ID, or {@code null} for guests
     */
    private Long extractUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return (Long) authentication.getPrincipal();
    }
}