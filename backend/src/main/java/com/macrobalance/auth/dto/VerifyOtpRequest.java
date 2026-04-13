package com.macrobalance.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request payload for verifying an OTP.
 *
 * <p>Used for OTP-based login ({@code POST /api/auth/login/otp}).
 * The identifier must match the one used when the OTP was requested.
 */
@Schema(description = "Request payload for OTP verification")
public record VerifyOtpRequest(

        @Schema(
                description = "Email address or phone number the OTP was sent to",
                example = "arjun@example.com"
        )
        String identifier,

        @Schema(
                description = "6-digit OTP received via email or SMS",
                example = "482910"
        )
        String otp

) {}