package com.macrobalance.cart.service;

import com.macrobalance.cart.entity.Cart;
import com.macrobalance.cart.entity.CartItem;
import com.macrobalance.cart.repository.CartItemRepository;
import com.macrobalance.cart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    private static final String ACTIVE = "ACTIVE";

    // 🔹 Get or Create Cart
    public Cart getOrCreateCart(Long userId, String guestId) {

        if (userId != null) {
            return cartRepository.findByUserIdAndStatus(userId, ACTIVE)
                    .orElseGet(() -> {
                        Cart cart = new Cart();
                        cart.setUserId(userId);
                        return cartRepository.save(cart);
                    });
        }

        return cartRepository.findByGuestIdAndStatus(guestId, ACTIVE)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setGuestId(guestId);
                    return cartRepository.save(cart);
                });
    }

    // 🔹 Add to Cart
    public void addToCart(Long userId, String guestId, Long productId, int quantity) {

        Cart cart = getOrCreateCart(userId, guestId);

        Optional<CartItem> existingItem =
                cartItemRepository.findByCartAndProductId(cart, productId);

        if (existingItem.isPresent()) {
            Cart item = cart;
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItemRepository.save(cartItem);
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProductId(productId);
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
    }

    // 🔹 Get Cart Items
    public List<CartItem> getCart(Long userId, String guestId) {

        Cart cart = getOrCreateCart(userId, guestId);
        return cartItemRepository.findByCart(cart);
    }

    // 🔥 Merge Cart (IMPORTANT)
    public void mergeCart(String guestId, Long userId) {

        Optional<Cart> guestCartOpt =
                cartRepository.findByGuestIdAndStatus(guestId, ACTIVE);

        if (guestCartOpt.isEmpty()) return;

        Cart guestCart = guestCartOpt.get();

        Cart userCart = cartRepository
                .findByUserIdAndStatus(userId, ACTIVE)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    return cartRepository.save(newCart);
                });

        List<CartItem> guestItems = cartItemRepository.findByCart(guestCart);

        for (CartItem guestItem : guestItems) {

            Optional<CartItem> existingItem =
                    cartItemRepository.findByCartAndProductId(userCart, guestItem.getProductId());

            if (existingItem.isPresent()) {
                CartItem item = existingItem.get();
                item.setQuantity(item.getQuantity() + guestItem.getQuantity());
                cartItemRepository.save(item);
            } else {
                guestItem.setCart(userCart);
                cartItemRepository.save(guestItem);
            }
        }

        guestCart.setStatus("MERGED");
        cartRepository.save(guestCart);
    }
}