package com.macrobalance.product.specification;

import com.macrobalance.product.dto.ProductFilterRequest;
import com.macrobalance.product.entity.Product;
import com.macrobalance.product.entity.ProductNutrition;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    // Private constructor — this is a utility class
    private ProductSpecification() {}

    public static Specification<Product> withFilters(ProductFilterRequest filters) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // Always filter active products
            predicates.add(cb.isTrue(root.get("isActive")));

            // Join nutrition table once — reused for all nutrition filters
            Join<Product, ProductNutrition> nutrition =
                    root.join("nutrition", JoinType.LEFT);

            // Keyword search on name
            if (filters.keyword() != null && !filters.keyword().isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("name")),
                        "%" + filters.keyword().toLowerCase() + "%"
                ));
            }

            // Category filter
            if (filters.categorySlug() != null && !filters.categorySlug().isBlank()) {
                predicates.add(cb.equal(
                        root.get("category").get("slug"),
                        filters.categorySlug()
                ));
            }

            // Price filter
            if (filters.maxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("price"), filters.maxPrice()
                ));
            }

            // Nutrition filters
            if (filters.minProtein() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        nutrition.get("protein"), filters.minProtein()
                ));
            }

            if (filters.maxSugar() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        nutrition.get("sugar"), filters.maxSugar()
                ));
            }

            if (filters.minFiber() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        nutrition.get("fiber"), filters.minFiber()
                ));
            }

            if (filters.maxCalories() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        nutrition.get("calories"), filters.maxCalories()
                ));
            }

            // Avoid duplicate rows from join
            query.distinct(true);

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}