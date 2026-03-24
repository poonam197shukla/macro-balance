package com.macrobalance.cart.repository;

import com.macrobalance.cart.entity.Cart;
import com.macrobalance.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCart(Cart cart);

    Optional<CartItem> findByCartAndProductId(Cart cart, Long productId);
}