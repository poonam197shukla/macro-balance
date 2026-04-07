package com.macrobalance.order.controller;

import com.macrobalance.common.dto.ApiResponse;
import com.macrobalance.order.dto.*;
import com.macrobalance.order.entity.OrderStatus;
import com.macrobalance.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // ── User endpoints ─────────────────────────────────────────

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<OrderResponse>> checkout(
            @RequestBody @Valid CheckoutRequest request,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();

        return ResponseEntity.ok(new ApiResponse<>(
                true, "Order placed successfully",
                orderService.checkout(userId, request)
        ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderSummaryResponse>>> getOrderHistory(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());

        return ResponseEntity.ok(new ApiResponse<>(
                true, "Orders fetched",
                orderService.getOrderHistory(userId, pageable)
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();

        return ResponseEntity.ok(new ApiResponse<>(
                true, "Order fetched",
                orderService.getOrderDetail(id, userId)
        ));
    }

    // ── Admin endpoints ────────────────────────────────────────

    @PatchMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status,
            Authentication authentication
    ) {
        // Use email as changedBy for audit trail
        String changedBy = authentication.getName();

        return ResponseEntity.ok(new ApiResponse<>(
                true, "Order status updated",
                orderService.updateOrderStatus(id, status, changedBy)
        ));
    }
}