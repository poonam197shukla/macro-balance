package com.macrobalance.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * Request payload for updating the authenticated user's profile.
 *
 * <p>Only {@code name} and {@code phone} can be changed.
 * Email and role are immutable through this endpoint.
 * Both fields are optional — omit a field to leave it unchanged.
 */
@Schema(description = "Request payload for updating the user's name and/or phone number")
public record UpdateProfileRequest(

        @Schema(
                description = "Updated display name (2–150 characters). Omit to keep current value.",
                example = "Arjun K. Sharma"
        )
        @Size(min = 2, max = 150)
        String name,

        @Schema(
                description = "Updated phone number (max 20 characters). " +
                        "Omit to keep current value. Updating resets isPhoneVerified to false.",
                example = "9876543210"
        )
        @Size(max = 20)
        String phone

) {}