package com.macrobalance.payment.controller;

import com.macrobalance.common.dto.ApiResponse;
import com.macrobalance.payment.dto.InitiatePaymentRequest;
import com.macrobalance.payment.dto.InitiatePaymentResponse;
import com.macrobalance.payment.dto.PaymentResponse;
import com.macrobalance.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for payment operations.
 *
 * <p>Manages the full Razorpay payment lifecycle:
 * <ol>
 *   <li>Frontend calls {@code POST /api/payments/initiate} after checkout
 *       to create a Razorpay order and receive the {@code razorpay_order_id}</li>
 *   <li>Frontend opens the Razorpay modal using the returned credentials</li>
 *   <li>On payment completion, Razorpay calls {@code POST /api/payments/webhook}
 *       which verifies the HMAC signature and transitions the order to CONFIRMED</li>
 * </ol>
 *
 * <p>The webhook endpoint is public — Razorpay servers call it directly without
 * a JWT. All other endpoints require authentication.
 *
 * <p>All payment events are written to the dedicated payment audit log at
 * {@code logs/macro-balance-backend-payments.log} with 90-day retention.
 */
@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Razorpay payment initiation, webhook handling, and payment status")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Creates a Razorpay order for the given MacroBalance order.
     *
     * <p>Must be called after {@code POST /api/orders/checkout}.
     * The returned {@code razorpayOrderId} and {@code keyId} are used by
     * the frontend to initialise the Razorpay checkout modal.
     *
     * <p>Amount is passed to Razorpay in paise (INR × 100) internally,
     * but returned to the frontend in rupees for display purposes.
     *
     * @param request        contains the MacroBalance order ID
     * @param authentication the current user's JWT context
     * @return Razorpay order credentials needed to open the payment modal
     */
    @Operation(
            summary = "Initiate payment",
            description = "Creates a Razorpay order for the given MacroBalance order ID. " +
                    "Call this after POST /api/orders/checkout. " +
                    "Use the returned razorpayOrderId and keyId to open the Razorpay checkout modal."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Razorpay order created — payment modal credentials returned"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Order not found, already paid, or does not belong to user"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "Unauthorized")
    })
    @SecurityRequirement(name = "Bearer Auth")
    @PostMapping("/initiate")
    public ResponseEntity<ApiResponse<InitiatePaymentResponse>> initiatePayment(
            @RequestBody @Valid InitiatePaymentRequest request,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Payment initiated",
                paymentService.initiatePayment(request.orderId(), userId)
        ));
    }

    /**
     * Razorpay webhook endpoint — called by Razorpay servers on payment events.
     *
     * <p><strong>This endpoint is intentionally public.</strong> It is called
     * by Razorpay, not by the frontend, so no JWT is present.
     *
     * <p>On each call, the service:
     * <ol>
     *   <li>Verifies the HMAC-SHA256 signature using the raw request body
     *       and the {@code razorpay.webhook-secret} from configuration</li>
     *   <li>Parses the event type from the JSON payload</li>
     *   <li>On {@code payment.captured} — marks the payment as SUCCESS
     *       and transitions the order to CONFIRMED</li>
     *   <li>On {@code payment.failed} — marks the payment as FAILED
     *       and records the failure reason</li>
     * </ol>
     *
     * <p>Returns HTTP 200 in all cases — Razorpay retries on non-200 responses,
     * so returning an error status would cause duplicate processing.
     *
     * <p>For local development, expose this endpoint via ngrok and register
     * the URL in the Razorpay dashboard under Webhooks.
     *
     * @param payload   raw JSON body from Razorpay — must not be parsed before
     *                  this method to preserve the exact bytes for HMAC verification
     * @param signature value of the {@code X-Razorpay-Signature} header
     * @return empty 200 response
     */
    @Operation(
            summary = "Razorpay webhook",
            description = "Called by Razorpay servers on payment events (payment.captured, payment.failed). " +
                    "Verifies HMAC-SHA256 signature before processing. " +
                    "This endpoint is public — do not call it directly. " +
                    "Register your ngrok URL in the Razorpay dashboard for local testing."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Webhook processed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Invalid signature — possible tampered payload")
    })
    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(
            @Parameter(description = "Raw JSON payload from Razorpay")
            @RequestBody String payload,
            @Parameter(description = "HMAC-SHA256 signature from X-Razorpay-Signature header", required = true)
            @RequestHeader("X-Razorpay-Signature") String signature
    ) {
        paymentService.handleWebhook(payload, signature);
        return ResponseEntity.ok().build();
    }

    /**
     * Returns the payment record for a given order.
     *
     * <p>The order must belong to the authenticated user.
     * Useful for displaying payment status on the order detail page,
     * especially when the user returns after completing payment.
     *
     * @param orderId        the MacroBalance order ID
     * @param authentication the current user's JWT context
     * @return payment details including Razorpay IDs and current status
     */
    @Operation(
            summary = "Get payment by order ID",
            description = "Returns the payment record for the given order. " +
                    "The order must belong to the authenticated user."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Payment fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Payment not found for this order")
    })
    @SecurityRequirement(name = "Bearer Auth")
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(
            @Parameter(description = "MacroBalance order ID", required = true)
            @PathVariable Long orderId,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Payment fetched",
                paymentService.getPaymentByOrderId(orderId, userId)
        ));
    }
}