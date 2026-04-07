package com.macrobalance.payment.repository;

import com.macrobalance.payment.entity.Payment;
import com.macrobalance.payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(Long orderId);

    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);

    List<Payment> findAllByOrderId(Long orderId);

    boolean existsByOrderIdAndStatus(Long orderId, PaymentStatus status);
}