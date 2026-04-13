package com.macrobalance.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for changing the authenticated user's password.
 *
 * <p>The current password is required for verification before the
 * new password is applied. Both fields are mandatory.
 */
@Schema(description = "Request payload for changing the authenticated user's password")
public record ChangePasswordRequest(

        @Schema(
                description = "The user's current password for verification",
                example = "OldPass@123"
        )
        @NotBlank
        String currentPassword,

        @Schema(
                description = "The new password to set. Minimum 8 characters.",
                example = "NewPass@2024"
        )
        @NotBlank
        @Size(min = 8, message = "Password must be at least 8 characters")
        String newPassword

) {}