package com.macrobalance.product.controller;

import com.macrobalance.common.dto.ApiResponse;
import com.macrobalance.product.dto.*;
import com.macrobalance.product.service.ProductService;
import com.macrobalance.product.service.ReviewService;
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
 * REST controller for product catalogue and product reviews.
 *
 * <p>Exposes three groups of endpoints:
 * <ul>
 *   <li><strong>Public</strong> — product listing with filtering, product detail by slug</li>
 *   <li><strong>Authenticated</strong> — submit, update, and delete reviews</li>
 *   <li><strong>Admin</strong> — create, update, and deactivate products</li>
 * </ul>
 *
 * <p>Products are identified by their URL-friendly {@code slug} on public endpoints
 * to avoid exposing numeric IDs and to support SEO-friendly URLs.
 * Admin endpoints use numeric IDs for precision.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product catalogue, filtering, and review management")
public class ProductController {

    private final ProductService productService;
    private final ReviewService reviewService;

    // ── Public endpoints ───────────────────────────────────────

    /**
     * Returns a paginated, filterable list of active products.
     *
     * <p>Supports filtering by category, nutritional values, price, and
     * keyword search. All filter parameters are optional — omitting them
     * returns all active products.
     *
     * <p>Nutritional filters use the per-100g values stored in
     * {@code ProductNutrition} and are implemented via JPA Specifications.
     *
     * @param filters query parameters for filtering (category, nutrition, price, keyword)
     * @param page    zero-based page index (default 0)
     * @param size    number of results per page (default 12)
     * @param sortBy  field name to sort by (default {@code id})
     * @return paginated list of product summaries with key macros
     */
    @Operation(
            summary = "List products",
            description = "Returns a paginated list of active products. Supports filtering by " +
                    "categorySlug, minProtein, maxSugar, minFiber, maxCalories, maxPrice, and keyword. " +
                    "All filters are optional. Returns lightweight summaries — use GET /api/products/{slug} " +
                    "for full detail including all nutritional values."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Products fetched successfully")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductSummaryResponse>>> getProducts(
            @ModelAttribute ProductFilterRequest filters,
            @Parameter(description = "Zero-based page index", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of products per page", example = "12")
            @RequestParam(defaultValue = "12") int size,
            @Parameter(description = "Field to sort by", example = "price")
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Products fetched",
                productService.getProducts(filters, pageable)
        ));
    }

    /**
     * Returns the full detail of a single product by its slug.
     *
     * <p>Includes all nutritional values, average rating, and review count.
     * Only active products are returned — deactivated products return 404.
     *
     * @param slug the URL-friendly product identifier
     * @return full product detail including nutrition and rating summary
     */
    @Operation(
            summary = "Get product by slug",
            description = "Returns full product detail including all nutritional values, " +
                    "average rating, review count, and stock level. " +
                    "Only active products are returned."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Product fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Product not found or inactive")
    })
    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(
            @Parameter(description = "URL-friendly product slug", example = "protein-bar-iron", required = true)
            @PathVariable String slug) {

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Product fetched",
                productService.getProductBySlug(slug)
        ));
    }

    // ── Reviews ────────────────────────────────────────────────

    /**
     * Returns a paginated list of reviews for a product, newest first.
     *
     * <p>Public endpoint — no authentication required.
     * Each review indicates whether it is from a verified purchaser.
     *
     * @param productId the product ID
     * @param page      zero-based page index (default 0)
     * @param size      number of reviews per page (default 10)
     * @return paginated reviews sorted by most recent first
     */
    @Operation(
            summary = "Get product reviews",
            description = "Returns paginated reviews for a product, sorted by most recent first. " +
                    "isVerifiedPurchase is set by the system when the reviewer has a DELIVERED order " +
                    "containing this product — it cannot be set manually."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Reviews fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{productId}/reviews")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getReviews(
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long productId,
            @Parameter(description = "Zero-based page index", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of reviews per page", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Reviews fetched",
                reviewService.getReviews(productId, pageable)
        ));
    }

    /**
     * Submits a new review for a product.
     *
     * <p>Requires authentication. Each user may only submit one review per product.
     * The {@code isVerifiedPurchase} flag is set automatically by the system
     * when the order containing this product is marked as DELIVERED — users
     * cannot set it themselves.
     *
     * <p>After creation, the product's {@code avgRating} and {@code reviewCount}
     * are recalculated and persisted.
     *
     * @param productId      the product ID to review
     * @param request        rating (1–5), optional title, and optional body
     * @param authentication the current user's JWT context
     * @return the created review
     */
    @Operation(
            summary = "Submit a review",
            description = "Submits a review for a product. One review per user per product. " +
                    "Rating must be 1–5. isVerifiedPurchase is set automatically — not by user input. " +
                    "Updates the product's average rating after submission."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Review submitted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Validation failed or user has already reviewed this product"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Product not found")
    })
    @SecurityRequirement(name = "Bearer Auth")
    @PostMapping("/{productId}/reviews")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @Parameter(description = "Product ID to review", required = true)
            @PathVariable Long productId,
            @RequestBody @Valid CreateReviewRequest request,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Review submitted",
                reviewService.createReview(productId, userId, request)
        ));
    }

    /**
     * Updates the authenticated user's existing review for a product.
     *
     * <p>Only the user who submitted the original review can update it.
     * The {@code isVerifiedPurchase} flag is not affected by updates.
     * Product average rating is recalculated after the update.
     *
     * @param productId      the product ID
     * @param request        updated rating, title, and body
     * @param authentication the current user's JWT context
     * @return the updated review
     */
    @Operation(
            summary = "Update a review",
            description = "Updates the authenticated user's existing review for a product. " +
                    "isVerifiedPurchase is unaffected by updates. " +
                    "Updates the product's average rating after saving."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Review updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Validation failed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Review not found for this user and product")
    })
    @SecurityRequirement(name = "Bearer Auth")
    @PutMapping("/{productId}/reviews")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long productId,
            @RequestBody @Valid CreateReviewRequest request,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Review updated",
                reviewService.updateReview(productId, userId, request)
        ));
    }

    /**
     * Deletes the authenticated user's review for a product.
     *
     * <p>Only the user who submitted the review can delete it.
     * Product average rating and review count are recalculated after deletion.
     *
     * @param productId      the product ID
     * @param authentication the current user's JWT context
     * @return empty success response
     */
    @Operation(
            summary = "Delete a review",
            description = "Deletes the authenticated user's review for a product. " +
                    "Recalculates the product's average rating and review count after deletion."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Review deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Review not found for this user and product")
    })
    @SecurityRequirement(name = "Bearer Auth")
    @DeleteMapping("/{productId}/reviews")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long productId,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        reviewService.deleteReview(productId, userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Review deleted", null));
    }

    // ── Admin endpoints ────────────────────────────────────────

    /**
     * Creates a new product with full nutritional information.
     *
     * <p>Requires the {@code ADMIN} role. The slug must be globally unique
     * and URL-safe. A {@link com.macrobalance.product.entity.ProductNutrition}
     * record is created atomically alongside the product.
     *
     * @param request product details including name, slug, price, stock, and nutrition
     * @return the created product with all fields including the assigned ID
     */
    @Operation(
            summary = "Create product (Admin)",
            description = "Creates a new product with nutritional data. " +
                    "The slug must be unique and URL-safe (lowercase, hyphen-separated). " +
                    "Nutrition data is required and created atomically with the product. " +
                    "Requires ADMIN role."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Product created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Validation failed or slug already exists"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403", description = "Forbidden — ADMIN role required")
    })
    @SecurityRequirement(name = "Bearer Auth")
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @RequestBody @Valid CreateProductRequest request) {

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Product created",
                productService.createProduct(request)
        ));
    }

    /**
     * Updates an existing product and its nutritional information.
     *
     * <p>Requires the {@code ADMIN} role. Full replacement — all fields
     * in the request overwrite the existing values. The slug may be changed
     * as long as it remains unique.
     *
     * @param id      the product ID to update
     * @param request updated product details and nutrition
     * @return the updated product
     */
    @Operation(
            summary = "Update product (Admin)",
            description = "Full replacement update of a product including its nutritional data. " +
                    "All fields are overwritten with the request values. " +
                    "Requires ADMIN role."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Product updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Validation failed or slug conflict"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403", description = "Forbidden — ADMIN role required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Product not found")
    })
    @SecurityRequirement(name = "Bearer Auth")
    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @Parameter(description = "Product ID to update", required = true)
            @PathVariable Long id,
            @RequestBody @Valid CreateProductRequest request) {

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Product updated",
                productService.updateProduct(id, request)
        ));
    }

    /**
     * Soft-deactivates a product, hiding it from the public catalogue.
     *
     * <p>Requires the {@code ADMIN} role. Sets {@code isActive = false} —
     * the product is not deleted and can be reactivated. Deactivated products
     * are excluded from {@code GET /api/products} results and return 404
     * on {@code GET /api/products/{slug}}.
     *
     * @param id the product ID to deactivate
     * @return empty success response
     */
    @Operation(
            summary = "Deactivate product (Admin)",
            description = "Soft-deletes a product by setting isActive = false. " +
                    "The product is hidden from the public catalogue but not permanently deleted. " +
                    "Requires ADMIN role."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Product deactivated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403", description = "Forbidden — ADMIN role required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Product not found")
    })
    @SecurityRequirement(name = "Bearer Auth")
    @PatchMapping("/admin/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivateProduct(
            @Parameter(description = "Product ID to deactivate", required = true)
            @PathVariable Long id) {

        productService.deactivateProduct(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Product deactivated", null));
    }
}