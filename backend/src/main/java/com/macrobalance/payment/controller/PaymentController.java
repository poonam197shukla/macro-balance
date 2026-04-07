package com.macrobalance.payment.controller;

import com.macrobalance.common.dto.ApiResponse;
import com.macrobalance.payment.dto.InitiatePaymentRequest;
import com.macrobalance.payment.dto.InitiatePaymentResponse;
import com.macrobalance.payment.dto.PaymentResponse;
import com.macrobalance.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // Called by frontend after checkout to get razorpay_order_id
    @PostMapping("/initiate")
    public ResponseEntity<ApiResponse<InitiatePaymentResponse>> initiatePayment(
            @RequestBody @Valid InitiatePaymentRequest request,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();

        return ResponseEntity.ok(new ApiResponse<>(
                true, "Payment initiated",
                paymentService.initiatePayment(request.orderId(), userId)
        ));
    }

    // Called by Razorpay servers — NO authentication
    // Raw String body needed for HMAC verification
    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature
    ) {
        paymentService.handleWebhook(payload, signature);
        return ResponseEntity.ok().build();
    }

    // Get payment status for an order
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(
            @PathVariable Long orderId,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();

        return ResponseEntity.ok(new ApiResponse<>(
                true, "Payment fetched",
                paymentService.getPaymentByOrderId(orderId, userId)
        ));
    }
}