package com.macrobalance.payment.service;

import com.macrobalance.common.exception.BadRequestException;
import com.macrobalance.order.entity.Order;
import com.macrobalance.order.entity.OrderStatus;
import com.macrobalance.order.repository.OrderRepository;
import com.macrobalance.order.service.OrderService;
import com.macrobalance.payment.dto.InitiatePaymentResponse;
import com.macrobalance.payment.dto.PaymentResponse;
import com.macrobalance.payment.entity.Payment;
import com.macrobalance.payment.entity.PaymentStatus;
import com.macrobalance.payment.repository.PaymentRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository  paymentRepository;
    private final OrderRepository    orderRepository;
    private final OrderService       orderService;
    private final RazorpayClient     razorpayClient;

    @Value("${razorpay.key-id}")
    private String razorpayKeyId;

    @Value("${razorpay.webhook-secret}")
    private String webhookSecret;

    // ── Initiate ───────────────────────────────────────────────

    @Transactional
    public InitiatePaymentResponse initiatePayment(Long orderId, Long userId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BadRequestException("Order not found"));

        // Ensure this order belongs to the requesting user
        if (!order.getUserId().equals(userId)) {
            throw new BadRequestException("Order does not belong to this user");
        }

        // Don't create duplicate payment if already successful
        if (paymentRepository.existsByOrderIdAndStatus(
                orderId, PaymentStatus.SUCCESS)) {
            throw new BadRequestException("Order already paid");
        }

        // Create Razorpay order
        // Razorpay expects amount in smallest currency unit (paise)
        int amountInPaise = order.getTotalAmount()
                .multiply(BigDecimal.valueOf(100))
                .intValue();

        try {
            JSONObject razorpayRequest = new JSONObject();
            razorpayRequest.put("amount", amountInPaise);
            razorpayRequest.put("currency", "INR");
            razorpayRequest.put("receipt", "order_" + orderId);

            com.razorpay.Order razorpayOrder =
                    razorpayClient.orders.create(razorpayRequest);

            String razorpayOrderId = razorpayOrder.get("id");

            // Save payment record
            Payment payment = new Payment();
            payment.setOrderId(orderId);
            payment.setRazorpayOrderId(razorpayOrderId);
            payment.setAmount(order.getTotalAmount());
            payment.setStatus(PaymentStatus.PENDING);
            paymentRepository.save(payment);

            return new InitiatePaymentResponse(
                    razorpayOrderId,
                    order.getTotalAmount(),
                    "INR",
                    razorpayKeyId
            );

        } catch (RazorpayException e) {
            log.error("Razorpay order creation failed for orderId={}: {}",
                    orderId, e.getMessage());
            throw new BadRequestException("Payment initiation failed");
        }
    }

    // ── Webhook ────────────────────────────────────────────────

    @Transactional
    public void handleWebhook(String payload, String razorpaySignature) {

        // 1. Verify webhook signature
        if (!verifyWebhookSignature(payload, razorpaySignature)) {
            log.warn("Invalid Razorpay webhook signature received");
            throw new BadRequestException("Invalid webhook signature");
        }

        JSONObject event = new JSONObject(payload);
        String eventType = event.getString("event");

        log.info("Razorpay webhook received: {}", eventType);

        switch (eventType) {
            case "payment.captured" -> handlePaymentCaptured(event);
            case "payment.failed"   -> handlePaymentFailed(event);
            default -> log.info("Unhandled webhook event: {}", eventType);
        }
    }

    // ── Query ──────────────────────────────────────────────────

    public PaymentResponse getPaymentByOrderId(Long orderId, Long userId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BadRequestException("Order not found"));

        if (!order.getUserId().equals(userId)) {
            throw new BadRequestException("Order does not belong to this user");
        }

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new BadRequestException(
                        "Payment not found for order: " + orderId));

        return toResponse(payment);
    }

    // ── Private helpers ────────────────────────────────────────

    private void handlePaymentCaptured(JSONObject event) {

        JSONObject paymentEntity = event
                .getJSONObject("payload")
                .getJSONObject("payment")
                .getJSONObject("entity");

        String razorpayOrderId   = paymentEntity.getString("order_id");
        String razorpayPaymentId = paymentEntity.getString("id");
        String signature         = paymentEntity.optString("signature", null);

        Payment payment = paymentRepository
                .findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() -> new BadRequestException(
                        "Payment not found for razorpay order: " + razorpayOrderId));

        payment.setRazorpayPaymentId(razorpayPaymentId);
        payment.setRazorpaySignature(signature);
        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);

        // Transition order: PENDING → CONFIRMED
        orderService.updateOrderStatus(
                payment.getOrderId(),
                OrderStatus.CONFIRMED,
                "RAZORPAY_WEBHOOK"
        );

        log.info("Payment captured for orderId={}, razorpayPaymentId={}",
                payment.getOrderId(), razorpayPaymentId);
    }

    private void handlePaymentFailed(JSONObject event) {

        JSONObject paymentEntity = event
                .getJSONObject("payload")
                .getJSONObject("payment")
                .getJSONObject("entity");

        String razorpayOrderId = paymentEntity.getString("order_id");
        String errorDescription = paymentEntity
                .optJSONObject("error_description") != null
                ? paymentEntity.getString("error_description")
                : "Payment failed";

        Payment payment = paymentRepository
                .findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() -> new BadRequestException(
                        "Payment not found for razorpay order: " + razorpayOrderId));

        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason(errorDescription);
        paymentRepository.save(payment);

        log.warn("Payment failed for orderId={}, reason={}",
                payment.getOrderId(), errorDescription);
    }

    private boolean verifyWebhookSignature(String payload, String signature) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(
                    webhookSecret.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            );
            mac.init(secretKey);
            byte[] hash = mac.doFinal(
                    payload.getBytes(StandardCharsets.UTF_8));

            String computed = HexFormat.of().formatHex(hash);
            return computed.equals(signature);

        } catch (Exception e) {
            log.error("Webhook signature verification failed", e);
            return false;
        }
    }

    private PaymentResponse toResponse(Payment p) {
        return new PaymentResponse(
                p.getId(),
                p.getOrderId(),
                p.getRazorpayOrderId(),
                p.getRazorpayPaymentId(),
                p.getStatus(),
                p.getAmount(),
                p.getCurrency(),
                p.getFailureReason(),
                p.getCreatedAt()
        );
    }
}