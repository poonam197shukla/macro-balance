package com.macrobalance.product.controller;

import com.macrobalance.common.dto.ApiResponse;
import com.macrobalance.product.dto.CategoryDto;
import com.macrobalance.product.dto.CreateCategoryRequest;
import com.macrobalance.product.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for product category management.
 *
 * <p>Categories organise the MacroBalance product catalogue
 * (e.g. Protein Bars, Date Bites, Roasted Makhana, Trail Mixes).
 * Each category has a URL-friendly {@code slug} used for filtering
 * products via {@code GET /api/products?categorySlug=protein-bars}.
 *
 * <p>Public endpoints return all active categories.
 * Admin endpoints allow creating, updating, and soft-deactivating categories.
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Product category listing and management")
public class CategoryController {

    private final CategoryService categoryService;

    // ── Public ─────────────────────────────────────────────────

    /**
     * Returns all active product categories.
     *
     * <p>Typically used to populate the category filter on the shop page.
     * Inactive categories are excluded.
     *
     * @return flat list of all active categories
     */
    @Operation(
            summary = "Get all categories",
            description = "Returns all active product categories. " +
                    "Use the slug field to filter products via GET /api/products?categorySlug={slug}."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Categories fetched successfully")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getAllCategories() {
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Categories fetched",
                categoryService.getAllCategories()
        ));
    }

    /**
     * Returns a single category by its slug.
     *
     * <p>Only active categories are returned.
     *
     * @param slug the URL-friendly category identifier
     * @return category details including name, slug, and description
     */
    @Operation(
            summary = "Get category by slug",
            description = "Returns a single active category by its URL-friendly slug."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Category fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Category not found or inactive")
    })
    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<CategoryDto>> getCategory(
            @Parameter(description = "URL-friendly category slug", example = "protein-bars", required = true)
            @PathVariable String slug) {

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Category fetched",
                categoryService.getCategoryBySlug(slug)
        ));
    }

    // ── Admin ──────────────────────────────────────────────────

    /**
     * Creates a new product category.
     *
     * <p>Requires the {@code ADMIN} role. Both {@code name} and {@code slug}
     * must be unique. The slug should be lowercase and hyphen-separated to
     * work correctly as a URL query parameter.
     *
     * @param request category name, slug, and optional description
     * @return the created category with its assigned ID
     */
    @Operation(
            summary = "Create category (Admin)",
            description = "Creates a new product category. Both name and slug must be unique. " +
                    "Slug should be lowercase and hyphen-separated (e.g. 'protein-bars'). " +
                    "Requires ADMIN role."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Category created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Validation failed or name/slug already exists"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403", description = "Forbidden — ADMIN role required")
    })
    @SecurityRequirement(name = "Bearer Auth")
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryDto>> createCategory(
            @RequestBody @Valid CreateCategoryRequest request) {

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Category created",
                categoryService.createCategory(request)
        ));
    }

    /**
     * Updates an existing category.
     *
     * <p>Requires the {@code ADMIN} role. Full replacement — all fields
     * are overwritten with the request values. The slug may be changed
     * as long as it remains unique.
     *
     * @param id      the category ID to update
     * @param request updated name, slug, and description
     * @return the updated category
     */
    @Operation(
            summary = "Update category (Admin)",
            description = "Full replacement update of a category. All fields are overwritten. " +
                    "Requires ADMIN role."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Category updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Validation failed or name/slug conflict"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403", description = "Forbidden — ADMIN role required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Category not found")
    })
    @SecurityRequirement(name = "Bearer Auth")
    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryDto>> updateCategory(
            @Parameter(description = "Category ID to update", required = true)
            @PathVariable Long id,
            @RequestBody @Valid CreateCategoryRequest request) {

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Category updated",
                categoryService.updateCategory(id, request)
        ));
    }

    /**
     * Soft-deactivates a category, hiding it from the public catalogue.
     *
     * <p>Requires the {@code ADMIN} role. Sets {@code isActive = false} —
     * the category is not deleted. Products belonging to a deactivated
     * category are also excluded from public listing results.
     *
     * @param id the category ID to deactivate
     * @return empty success response
     */
    @Operation(
            summary = "Deactivate category (Admin)",
            description = "Soft-deletes a category by setting isActive = false. " +
                    "Products in this category will also be hidden from public listings. " +
                    "Requires ADMIN role."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Category deactivated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403", description = "Forbidden — ADMIN role required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Category not found")
    })
    @SecurityRequirement(name = "Bearer Auth")
    @PatchMapping("/admin/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivateCategory(
            @Parameter(description = "Category ID to deactivate", required = true)
            @PathVariable Long id) {

        categoryService.deactivateCategory(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Category deactivated", null));
    }
}