package com.macrobalance.product.repository;

import com.macrobalance.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository
        extends JpaRepository<Product, Long>,
        JpaSpecificationExecutor<Product> {          // ← for dynamic filters

    Optional<Product> findBySlug(String slug);

    boolean existsBySlug(String slug);

    // Fetch product + nutrition in one query (avoids N+1)
    @Query("""
            SELECT p FROM Product p
            LEFT JOIN FETCH p.nutrition
            WHERE p.slug = :slug
            AND p.isActive = true
            """)
    Optional<Product> findActiveBySlugWithNutrition(@Param("slug") String slug);

    // For listing — nutrition fetched separately in batch
    Page<Product> findAllByIsActiveTrue(Pageable pageable);
}