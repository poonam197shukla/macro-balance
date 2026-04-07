package com.macrobalance.product.repository;

import com.macrobalance.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findBySlug(String slug);

    List<Category> findAllByIsActiveTrue();

    boolean existsBySlug(String slug);

    boolean existsByName(String name);
}