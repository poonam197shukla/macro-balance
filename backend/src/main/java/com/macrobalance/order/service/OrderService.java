package com.macrobalance.order.service;

import com.macrobalance.cart.entity.Cart;
import com.macrobalance.cart.entity.CartItem;
import com.macrobalance.cart.repository.CartItemRepository;
import com.macrobalance.cart.repository.CartRepository;
import com.macrobalance.common.exception.BadRequestException;
import com.macrobalance.order.dto.*;
import com.macrobalance.order.entity.*;
import com.macrobalance.order.repository.OrderRepository;
import com.macrobalance.product.entity.Product;
import com.macrobalance.product.repository.ProductRepository;
import com.macrobalance.product.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository     orderRepository;
    private final CartRepository      cartRepository;
    private final CartItemRepository  cartItemRepository;
    private final ProductRepository   productRepository;
    private final ReviewService       reviewService;

    private static final BigDecimal SHIPPING_CHARGE = new BigDecimal("49.00");
    private static final BigDecimal FREE_SHIPPING_THRESHOLD = new BigDecimal("500.00");

    @Transactional
    public OrderResponse checkout(Long userId, CheckoutRequest request) {

        // 1. Get active cart
        Cart cart = cartRepository
                .findByUserIdAndStatus(userId, "ACTIVE")
                .orElseThrow(() -> new BadRequestException("No active cart found"));

        List<CartItem> cartItems = cartItemRepository.findByCart(cart);

        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        // 2. Build order
        Order order = new Order();
        order.setUserId(userId);
        order.setAddressId(request.addressId());
        order.setNotes(request.notes());

        BigDecimal subtotal = BigDecimal.ZERO;

        // 3. Validate stock and create order items
        for (CartItem cartItem : cartItems) {

            Product product = productRepository
                    .findById(cartItem.getProductId())
                    .orElseThrow(() -> new BadRequestException(
                            "Product not found: " + cartItem.getProductId()));

            if (!product.isActive()) {
                throw new BadRequestException(
                        product.getName() + " is no longer available");
            }

            if (product.getStock() < cartItem.getQuantity()) {
                throw new BadRequestException(
                        "Insufficient stock for: " + product.getName());
            }

            // Deduct stock
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            // Snapshot order item
            OrderItem item = new OrderItem();
            item.setProductId(product.getId());
            item.setProductName(product.getName());   // snapshot
            item.setQuantity(cartItem.getQuantity());
            item.setPriceAtPurchase(product.getPrice());

            order.addItem(item);

            subtotal = subtotal.add(
                    product.getPrice()
                            .multiply(BigDecimal.valueOf(cartItem.getQuantity()))
            );
        }

        // 4. Apply shipping
        BigDecimal shipping = subtotal.compareTo(FREE_SHIPPING_THRESHOLD) >= 0
                ? BigDecimal.ZERO
                : SHIPPING_CHARGE;

        order.setTotalAmount(subtotal.add(shipping));

        // 5. Record initial status history
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOldStatus(null);
        history.setNewStatus(OrderStatus.PENDING);
        history.setChangedBy("SYSTEM");
        order.addStatusHistory(history);

        orderRepository.save(order);

        // 6. Clear the cart
        cart.setStatus("CHECKED_OUT");
        cartRepository.save(cart);

        return toDetailResponse(order);
    }

    public Page<OrderSummaryResponse> getOrderHistory(Long userId, Pageable pageable) {
        return orderRepository
                .findByUserId(userId, pageable)
                .map(this::toSummaryResponse);
    }

    public OrderResponse getOrderDetail(Long orderId, Long userId) {
        Order order = orderRepository
                .findByIdAndUserIdWithItems(orderId, userId)
                .orElseThrow(() -> new BadRequestException("Order not found"));

        return toDetailResponse(order);
    }

    // ── Admin ──────────────────────────────────────────────────

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId,
                                           OrderStatus newStatus,
                                           String changedBy) {

        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new BadRequestException("Order not found"));

        OrderStatus oldStatus = order.getStatus();

        // Guard against invalid transitions
        validateStatusTransition(oldStatus, newStatus);

        order.setStatus(newStatus);

        OrderStatusHistory history = new OrderStatusHistory();
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setChangedBy(changedBy);
        order.addStatusHistory(history);

        orderRepository.save(order);

        // When delivered — mark verified purchases on reviews
        if (newStatus == OrderStatus.DELIVERED) {
            order.getItems().forEach(item ->
                    reviewService.markVerifiedPurchase(
                            item.getProductId(), order.getUserId())
            );
        }

        // When cancelled — restore stock
        if (newStatus == OrderStatus.CANCELLED) {
            restoreStock(order.getItems());
        }

        return toDetailResponse(order);
    }

    // ── Private helpers ────────────────────────────────────────

    private void validateStatusTransition(OrderStatus from, OrderStatus to) {

        boolean valid = switch (from) {
            case PENDING    -> to == OrderStatus.CONFIRMED
                    || to == OrderStatus.CANCELLED;
            case CONFIRMED  -> to == OrderStatus.PROCESSING
                    || to == OrderStatus.CANCELLED;
            case PROCESSING -> to == OrderStatus.SHIPPED;
            case SHIPPED    -> to == OrderStatus.DELIVERED;
            default         -> false;
        };

        if (!valid) {
            throw new BadRequestException(
                    "Invalid status transition: " + from + " → " + to);
        }
    }

    private void restoreStock(List<OrderItem> items) {
        items.forEach(item ->
                productRepository.findById(item.getProductId()).ifPresent(product -> {
                    product.setStock(product.getStock() + item.getQuantity());
                    productRepository.save(product);
                })
        );
    }

    // ── Mappers ────────────────────────────────────────────────

    private OrderResponse toDetailResponse(Order o) {
        List<OrderItemResponse> items = o.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getPriceAtPurchase(),
                        item.getPriceAtPurchase()
                                .multiply(BigDecimal.valueOf(item.getQuantity()))
                ))
                .toList();

        return new OrderResponse(
                o.getId(),
                o.getStatus(),
                o.getTotalAmount(),
                o.getAddressId(),
                o.getNotes(),
                items,
                o.getCreatedAt()
        );
    }

    private OrderSummaryResponse toSummaryResponse(Order o) {
        return new OrderSummaryResponse(
                o.getId(),
                o.getStatus(),
                o.getTotalAmount(),
                o.getItems().size(),
                o.getCreatedAt()
        );
    }
}