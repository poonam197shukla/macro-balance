package com.macrobalance.order.controller;

import com.macrobalance.common.dto.ApiResponse;
import com.macrobalance.order.dto.*;
import com.macrobalance.order.entity.OrderStatus;
import com.macrobalance.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for order management.
 *
 * <p>Covers the full order lifecycle from checkout through delivery:
 * <ul>
 *   <li>Users can place orders, view history, and track individual orders</li>
 *   <li>Admins can transition order status through the defined lifecycle</li>
 * </ul>
 *
 * <p>All endpoints require authentication. Admin endpoints additionally
 * require the {@code ADMIN} role enforced via {@code @PreAuthorize}.
 *
 * <p>Order status flow:
 * <pre>
 * PENDING → CONFIRMED → PROCESSING → SHIPPED → DELIVERED
 * PENDING → CANCELLED
 * CONFIRMED → CANCELLED
 * </pre>
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order placement, history, and lifecycle management")
@SecurityRequirement(name = "Bearer Auth")
public class OrderController {

    private final OrderService orderService;

    // ── User endpoints ─────────────────────────────────────────

    /**
     * Places an order from the user's active cart.
     *
     * <p>This endpoint atomically:
     * <ol>
     *   <li>Validates stock for every item in the cart</li>
     *   <li>Deducts stock from each product</li>
     *   <li>Snapshots product names and prices into order items</li>
     *   <li>Calculates the total with shipping (free above ₹500)</li>
     *   <li>Creates the order in {@code PENDING} status</li>
     *   <li>Marks the cart as {@code CHECKED_OUT}</li>
     * </ol>
     *
     * <p>Payment is handled separately via {@code POST /api/payments/initiate}.
     *
     * @param request        delivery address ID and optional notes
     * @param authentication the current user's JWT context
     * @return the created order with all items and total amount
     */
    @Operation(
            summary = "Place order (checkout)",
            description = "Converts the user's active cart into an order. Validates stock, " +
                    "deducts inventory, and creates the order in PENDING status. " +
                    "Free shipping on orders above ₹500, otherwise ₹49 shipping is added. " +
                    "Call POST /api/payments/initiate after this to initiate payment."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Order placed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Cart is empty, insufficient stock, or product no longer available"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<OrderResponse>> checkout(
            @RequestBody @Valid CheckoutRequest request,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Order placed successfully",
                orderService.checkout(userId, request)
        ));
    }

    /**
     * Returns a paginated list of the authenticated user's past orders,
     * sorted by most recent first.
     *
     * <p>Returns lightweight summary objects — use {@code GET /api/orders/{id}}
     * for full order details including individual items.
     *
     * @param page           zero-based page index (default 0)
     * @param size           number of results per page (default 10)
     * @param authentication the current user's JWT context
     * @return paginated list of order summaries
     */
    @Operation(
            summary = "Get order history",
            description = "Returns a paginated list of the user's past orders, newest first. " +
                    "Use GET /api/orders/{id} to fetch full item details for a specific order."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Orders fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderSummaryResponse>>> getOrderHistory(
            @Parameter(description = "Zero-based page index", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of orders per page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Orders fetched",
                orderService.getOrderHistory(userId, pageable)
        ));
    }

    /**
     * Returns full details of a specific order including all line items.
     *
     * <p>The order must belong to the authenticated user.
     *
     * @param id             the order ID
     * @param authentication the current user's JWT context
     * @return full order detail including items, totals, and status
     */
    @Operation(
            summary = "Get order detail",
            description = "Returns the full details of a specific order including all line items, " +
                    "prices at purchase time, and the delivery address ID. " +
                    "The order must belong to the authenticated user."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Order fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Order not found or does not belong to this user")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(
            @Parameter(description = "Order ID", required = true)
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Order fetched",
                orderService.getOrderDetail(id, userId)
        ));
    }

    // ── Admin endpoints ────────────────────────────────────────

    /**
     * Transitions an order to a new status.
     *
     * <p>Requires the {@code ADMIN} role. Only valid transitions are allowed —
     * attempting an invalid transition returns a 400 error.
     *
     * <p>Side effects triggered by specific transitions:
     * <ul>
     *   <li>{@code → DELIVERED} — verified purchase flag is set on any existing
     *       reviews for the ordered products</li>
     *   <li>{@code → CANCELLED} — stock is restored for all order items</li>
     * </ul>
     *
     * <p>Valid transitions:
     * <pre>
     * PENDING    → CONFIRMED | CANCELLED
     * CONFIRMED  → PROCESSING | CANCELLED
     * PROCESSING → SHIPPED
     * SHIPPED    → DELIVERED
     * </pre>
     *
     * @param id             the order ID to update
     * @param status         the new target status
     * @param authentication the admin user's JWT context (email used as audit trail)
     * @return the updated order with the new status
     */
    @Operation(
            summary = "Update order status (Admin)",
            description = "Transitions the order to a new status. Only valid transitions are permitted. " +
                    "DELIVERED triggers verified purchase on reviews. CANCELLED restores stock. " +
                    "Valid flow: PENDING → CONFIRMED → PROCESSING → SHIPPED → DELIVERED. " +
                    "Requires ADMIN role."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Order status updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Invalid status transition"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403", description = "Forbidden — ADMIN role required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Order not found")
    })
    @PatchMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(
            @Parameter(description = "Order ID to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Target order status", required = true)
            @RequestParam OrderStatus status,
            Authentication authentication
    ) {
        String changedBy = authentication.getName();
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Order status updated",
                orderService.updateOrderStatus(id, status, changedBy)
        ));
    }
}