package com.macrobalance.product.controller;

import com.macrobalance.common.dto.ApiResponse;
import com.macrobalance.product.dto.*;
import com.macrobalance.product.service.ProductService;
import com.macrobalance.product.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ReviewService reviewService;

    // ── Public endpoints ───────────────────────────────────────

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductSummaryResponse>>> getProducts(
            @ModelAttribute ProductFilterRequest filters,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(new ApiResponse<>(
                true, "Products fetched",
                productService.getProducts(filters, pageable)
        ));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(
            @PathVariable String slug) {

        return ResponseEntity.ok(new ApiResponse<>(
                true, "Product fetched",
                productService.getProductBySlug(slug)
        ));
    }

    // ── Reviews ────────────────────────────────────────────────

    @GetMapping("/{productId}/reviews")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());

        return ResponseEntity.ok(new ApiResponse<>(
                true, "Reviews fetched",
                reviewService.getReviews(productId, pageable)
        ));
    }

    @PostMapping("/{productId}/reviews")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @PathVariable Long productId,
            @RequestBody @Valid CreateReviewRequest request,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();

        return ResponseEntity.ok(new ApiResponse<>(
                true, "Review submitted",
                reviewService.createReview(productId, userId, request)
        ));
    }

    @PutMapping("/{productId}/reviews")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @PathVariable Long productId,
            @RequestBody @Valid CreateReviewRequest request,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();

        return ResponseEntity.ok(new ApiResponse<>(
                true, "Review updated",
                reviewService.updateReview(productId, userId, request)
        ));
    }

    @DeleteMapping("/{productId}/reviews")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Long productId,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        reviewService.deleteReview(productId, userId);

        return ResponseEntity.ok(new ApiResponse<>(true, "Review deleted", null));
    }

    // ── Admin endpoints ────────────────────────────────────────

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @RequestBody @Valid CreateProductRequest request) {

        return ResponseEntity.ok(new ApiResponse<>(
                true, "Product created",
                productService.createProduct(request)
        ));
    }

    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @RequestBody @Valid CreateProductRequest request) {

        return ResponseEntity.ok(new ApiResponse<>(
                true, "Product updated",
                productService.updateProduct(id, request)
        ));
    }

    @PatchMapping("/admin/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivateProduct(
            @PathVariable Long id) {

        productService.deactivateProduct(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Product deactivated", null));
    }
}