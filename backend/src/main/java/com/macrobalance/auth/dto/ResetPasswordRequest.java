package com.macrobalance.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request payload for resetting a user's password via OTP verification.
 *
 * <p>Requires the user to have already requested an OTP via
 * {@code POST /api/auth/send-otp} using the same identifier.
 */
@Schema(description = "Request payload for OTP-based password reset")
public record ResetPasswordRequest(

        @Schema(
                description = "Email address or phone number that the OTP was sent to",
                example = "arjun@example.com"
        )
        String identifier,

        @Schema(
                description = "6-digit OTP received via email or SMS",
                example = "482910"
        )
        String otp,

        @Schema(
                description = "The new password to set. Minimum 8 characters.",
                example = "NewPass@2024"
        )
        String newPassword

) {}