package com.macrobalance.cart.repository;

import com.macrobalance.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserIdAndStatus(Long userId, String status);

    Optional<Cart> findByGuestIdAndStatus(String guestId, String status);
}
