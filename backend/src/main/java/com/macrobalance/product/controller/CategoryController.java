package com.macrobalance.product.controller;

import com.macrobalance.common.dto.ApiResponse;
import com.macrobalance.product.dto.CategoryDto;
import com.macrobalance.product.dto.CreateCategoryRequest;
import com.macrobalance.product.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // ── Public ─────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getAllCategories() {
        return ResponseEntity.ok(new ApiResponse<>(
                true, "Categories fetched",
                categoryService.getAllCategories()
        ));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<CategoryDto>> getCategory(
            @PathVariable String slug) {

        return ResponseEntity.ok(new ApiResponse<>(
                true, "Category fetched",
                categoryService.getCategoryBySlug(slug)
        ));
    }

    // ── Admin ──────────────────────────────────────────────────

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryDto>> createCategory(
            @RequestBody @Valid CreateCategoryRequest request) {

        return ResponseEntity.ok(new ApiResponse<>(
                true, "Category created",
                categoryService.createCategory(request)
        ));
    }

    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryDto>> updateCategory(
            @PathVariable Long id,
            @RequestBody @Valid CreateCategoryRequest request) {

        return ResponseEntity.ok(new ApiResponse<>(
                true, "Category updated",
                categoryService.updateCategory(id, request)
        ));
    }

    @PatchMapping("/admin/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivateCategory(
            @PathVariable Long id) {

        categoryService.deactivateCategory(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Category deactivated", null));
    }
}