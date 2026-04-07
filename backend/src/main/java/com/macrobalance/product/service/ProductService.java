package com.macrobalance.product.service;

import com.macrobalance.common.exception.BadRequestException;
import com.macrobalance.product.dto.*;
import com.macrobalance.product.entity.*;
import com.macrobalance.product.repository.*;
import com.macrobalance.product.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository     productRepository;
    private final CategoryRepository    categoryRepository;

    // ── Public APIs ────────────────────────────────────────────

    public Page<ProductSummaryResponse> getProducts(
            ProductFilterRequest filters, Pageable pageable) {

        return productRepository
                .findAll(ProductSpecification.withFilters(filters), pageable)
                .map(this::toSummaryResponse);
    }

    public ProductResponse getProductBySlug(String slug) {

        Product product = productRepository
                .findActiveBySlugWithNutrition(slug)
                .orElseThrow(() -> new BadRequestException(
                        "Product not found: " + slug));

        return toDetailResponse(product);
    }

    // ── Admin APIs ─────────────────────────────────────────────

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {

        if (productRepository.existsBySlug(request.slug())) {
            throw new BadRequestException("Slug already in use: " + request.slug());
        }

        Category category = categoryRepository
                .findById(request.categoryId())
                .orElseThrow(() -> new BadRequestException("Category not found"));

        Product product = new Product();
        product.setName(request.name());
        product.setSlug(request.slug());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setCategory(category);

        // Attach nutrition
        ProductNutrition nutrition = toNutritionEntity(request.nutrition());
        product.setNutrition(nutrition);  // helper method sets bidirectional ref

        productRepository.save(product);

        return toDetailResponse(product);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, CreateProductRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Product not found"));

        product.setName(request.name());
        product.setSlug(request.slug());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());

        // Update nutrition in place — same entity, just update fields
        ProductNutrition nutrition = product.getNutrition();
        if (nutrition == null) {
            nutrition = new ProductNutrition();
            product.setNutrition(nutrition);
        }
        applyNutritionUpdate(nutrition, request.nutrition());

        return toDetailResponse(product);
    }

    @Transactional
    public void deactivateProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Product not found"));
        product.setActive(false);
    }

    // ── Mappers ────────────────────────────────────────────────

    private ProductSummaryResponse toSummaryResponse(Product p) {

        BigDecimal protein = BigDecimal.ZERO;
        BigDecimal fiber   = BigDecimal.ZERO;
        BigDecimal sugar   = BigDecimal.ZERO;

        if (p.getNutrition() != null) {
            protein = p.getNutrition().getProtein();
            fiber   = p.getNutrition().getFiber();
            sugar   = p.getNutrition().getSugar();
        }

        return new ProductSummaryResponse(
                p.getId(),
                p.getName(),
                p.getSlug(),
                p.getPrice(),
                p.getCategory().getName(),
                p.getAvgRating(),
                p.getReviewCount(),
                protein,
                fiber,
                sugar
        );
    }

    private ProductResponse toDetailResponse(Product p) {

        NutritionDto nutritionDto = null;

        if (p.getNutrition() != null) {
            ProductNutrition n = p.getNutrition();
            nutritionDto = new NutritionDto(
                    n.getServingSizeG(),
                    n.getCalories(),
                    n.getProtein(),
                    n.getCarbs(),
                    n.getFiber(),
                    n.getSugar(),
                    n.getFat()
            );
        }

        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getSlug(),
                p.getDescription(),
                p.getPrice(),
                p.getStock(),
                p.getCategory().getName(),
                p.getAvgRating(),
                p.getReviewCount(),
                nutritionDto
        );
    }

    private ProductNutrition toNutritionEntity(NutritionDto dto) {
        ProductNutrition n = new ProductNutrition();
        applyNutritionUpdate(n, dto);
        return n;
    }

    private void applyNutritionUpdate(ProductNutrition n, NutritionDto dto) {
        if (dto == null) return;
        n.setServingSizeG(dto.servingSizeG());
        n.setCalories(dto.calories());
        n.setProtein(dto.protein());
        n.setCarbs(dto.carbs());
        n.setFiber(dto.fiber());
        n.setSugar(dto.sugar());
        n.setFat(dto.fat());
    }
}