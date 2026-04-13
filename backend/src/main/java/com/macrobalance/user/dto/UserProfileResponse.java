package com.macrobalance.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response payload representing the authenticated user's profile.
 *
 * <p>Returned by {@code GET /api/users/me} and {@code PUT /api/users/me}.
 * Sensitive fields such as password hash are never included.
 */
@Schema(description = "The authenticated user's profile")
public record UserProfileResponse(

        @Schema(description = "Unique user ID", example = "1")
        Long id,

        @Schema(description = "User's display name", example = "Arjun Sharma")
        String name,

        @Schema(description = "User's email address", example = "arjun@example.com")
        String email,

        @Schema(description = "User's phone number", example = "9876543210")
        String phone,

        @Schema(
                description = "Whether the user's email address has been verified via OTP",
                example = "true"
        )
        boolean isEmailVerified,

        @Schema(
                description = "Whether the user's phone number has been verified via OTP",
                example = "false"
        )
        boolean isPhoneVerified,

        @Schema(
                description = "User's role — determines access level",
                example = "USER",
                allowableValues = {"USER", "ADMIN"}
        )
        String role

) {}