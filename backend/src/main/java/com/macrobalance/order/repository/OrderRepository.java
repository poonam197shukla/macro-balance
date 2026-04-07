package com.macrobalance.order.repository;

import com.macrobalance.order.entity.Order;
import com.macrobalance.order.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByUserId(Long userId, Pageable pageable);

    // Fetch order with items in one query
    @Query("""
            SELECT o FROM Order o
            LEFT JOIN FETCH o.items
            WHERE o.id = :id
            AND o.userId = :userId
            """)
    Optional<Order> findByIdAndUserIdWithItems(
            @Param("id") Long id,
            @Param("userId") Long userId);

    // Admin — fetch any order with items
    @Query("""
            SELECT o FROM Order o
            LEFT JOIN FETCH o.items
            WHERE o.id = :id
            """)
    Optional<Order> findByIdWithItems(@Param("id") Long id);
}