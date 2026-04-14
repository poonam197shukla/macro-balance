package com.macrobalance.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request payload for creating a new user account.
 *
 * <p>Used by {@code POST /api/auth/register}.
 * Email must be unique. Phone is optional but must be unique if provided.
 * The password is BCrypt-hashed before storage.
 * A JWT token is returned immediately on success.
 */
@Schema(description = "Request payload for registering a new user account")
public record RegisterRequest(

        @Schema(description = "Full display name", example = "Arjun Sharma")
        String name,

        @Schema(description = "Email address — must be unique", example = "arjun@example.com")
        String email,

        @Schema(
                description = "Mobile phone number — optional, must be unique if provided",
                example = "9876543210"
        )
        String phone,

        @Schema(description = "Password — minimum 8 characters recommended", example = "Password@123")
        String password

) {}