package com.macrobalance.cart.entity;

import com.macrobalance.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * JPA entity representing a single line item within a cart.
 *
 * <p>Each cart item links a {@link Cart} to a product (by ID) with
 * a specified quantity. The product is referenced by ID rather than
 * a JPA relationship to keep reads lightweight and avoid accidental
 * eager loading of product data during cart operations.
 *
 * <p>The unique constraint on {@code (cart_id, product_id)} ensures
 * a product can only appear once per cart — adding the same product
 * again increments the quantity of the existing item rather than
 * creating a duplicate.
 *
 * <p>Extends {@link BaseEntity} for {@code id}, audit timestamps,
 * {@code createdBy}, {@code updatedBy}, and optimistic locking via {@code version}.
 */
@Entity
@Getter
@Setter
@Table(
        name = "cart_items",
        uniqueConstraints = @UniqueConstraint(
                name = "unique_cart_product",
                columnNames = {"cart_id", "product_id"}
        )
)
public class CartItem extends BaseEntity {

    /**
     * The cart this item belongs to.
     * Loaded lazily to avoid unnecessary joins when only item
     * metadata is needed.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    /**
     * ID of the product added to the cart.
     * Stored as a plain Long rather than a {@code @ManyToOne} relationship
     * to keep cart reads decoupled from the product module.
     */
    @Column(name = "product_id", nullable = false)
    private Long productId;

    /**
     * Number of units of the product in the cart.
     * Must be at least 1. Validated at the DTO layer.
     * Stock availability is checked only at checkout time,
     * not when items are added to the cart.
     */
    @Column(nullable = false)
    private Integer quantity;
}