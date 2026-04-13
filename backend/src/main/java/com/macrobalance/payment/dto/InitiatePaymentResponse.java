package com.macrobalance.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Response payload containing the credentials needed to open the Razorpay checkout modal.
 *
 * <p>The frontend uses these values to initialise the Razorpay SDK:
 * <pre>
 * const options = {
 *   key: keyId,
 *   amount: amount * 100,   // convert to paise
 *   currency: currency,
 *   order_id: razorpayOrderId,
 *   ...
 * };
 * const rzp = new Razorpay(options);
 * rzp.open();
 * </pre>
 */
@Schema(description = "Razorpay credentials required to open the payment modal on the frontend")
public record InitiatePaymentResponse(

        @Schema(
                description = "Razorpay order ID — pass this as 'order_id' when initialising the Razorpay SDK",
                example = "order_QbXk9mNpLrTzW1"
        )
        String razorpayOrderId,

        @Schema(
                description = "Order amount in INR (rupees, not paise). " +
                        "Multiply by 100 when passing to the Razorpay SDK.",
                example = "547.00"
        )
        BigDecimal amount,

        @Schema(
                description = "Currency code — always INR for MacroBalance",
                example = "INR"
        )
        String currency,

        @Schema(
                description = "Razorpay public key ID — pass this as 'key' when initialising the Razorpay SDK. " +
                        "Safe to expose to the frontend.",
                example = "rzp_test_xxxxxxxxxxxx"
        )
        String keyId

) {}