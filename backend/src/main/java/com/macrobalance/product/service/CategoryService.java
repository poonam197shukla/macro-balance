package com.macrobalance.product.service;

import com.macrobalance.common.exception.BadRequestException;
import com.macrobalance.product.dto.CategoryDto;
import com.macrobalance.product.dto.CreateCategoryRequest;
import com.macrobalance.product.entity.Category;
import com.macrobalance.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAllByIsActiveTrue()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public CategoryDto getCategoryBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .map(this::toDto)
                .orElseThrow(() -> new BadRequestException(
                        "Category not found: " + slug));
    }

    @Transactional
    public CategoryDto createCategory(CreateCategoryRequest request) {

        if (categoryRepository.existsBySlug(request.slug())) {
            throw new BadRequestException(
                    "Slug already in use: " + request.slug());
        }

        if (categoryRepository.existsByName(request.name())) {
            throw new BadRequestException(
                    "Category name already exists: " + request.name());
        }

        Category category = new Category();
        category.setName(request.name());
        category.setSlug(request.slug());
        category.setDescription(request.description());

        return toDto(categoryRepository.save(category));
    }

    @Transactional
    public CategoryDto updateCategory(Long id, CreateCategoryRequest request) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(
                        "Category not found"));

        category.setName(request.name());
        category.setSlug(request.slug());
        category.setDescription(request.description());

        return toDto(categoryRepository.save(category));
    }

    @Transactional
    public void deactivateCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(
                        "Category not found"));
        category.setActive(false);
    }

    // ── Mapper ─────────────────────────────────────────────────

    private CategoryDto toDto(Category c) {
        return new CategoryDto(
                c.getId(),
                c.getName(),
                c.getSlug(),
                c.getDescription(),
                c.isActive()
        );
    }
}