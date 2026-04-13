package com.macrobalance.payment.config;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for the Razorpay Java SDK client.
 *
 * <p>Provides a singleton {@link RazorpayClient} bean wired with the
 * API credentials from {@code application.yml}. The client is used by
 * {@code PaymentService} to create Razorpay orders.
 *
 * <p>Required properties in {@code application.yml}:
 * <pre>
 * razorpay:
 *   key-id: rzp_test_xxxxxxxxxxxx
 *   key-secret: xxxxxxxxxxxxxxxxxxxx
 *   webhook-secret: your_webhook_secret
 * </pre>
 *
 * <p>For production, these values must be provided via environment variables
 * and never hardcoded. See README for environment variable guidance.
 */
@Configuration
public class RazorpayConfig {

    /**
     * Razorpay API key ID.
     * Use test key ({@code rzp_test_*}) for development and staging,
     * live key ({@code rzp_live_*}) for production.
     */
    @Value("${razorpay.key-id}")
    private String keyId;

    /**
     * Razorpay API key secret.
     * Used to authenticate API calls to Razorpay.
     * Must be kept confidential — never exposed in responses or logs.
     */
    @Value("${razorpay.key-secret}")
    private String keySecret;

    /**
     * Creates and returns the {@link RazorpayClient} singleton.
     *
     * <p>The client is initialised once at application startup and reused
     * for all Razorpay API calls throughout the application lifetime.
     *
     * @return configured Razorpay client
     * @throws RazorpayException if the client cannot be initialised
     *         (e.g. invalid credentials format)
     */
    @Bean
    public RazorpayClient razorpayClient() throws RazorpayException {
        return new RazorpayClient(keyId, keySecret);
    }
}