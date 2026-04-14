package com.macrobalance.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request payload for email and password authentication.
 *
 * <p>Used by {@code POST /api/auth/login}.
 * On success, a JWT token is returned valid for 24 hours.
 */
@Schema(description = "Request payload for email and password login")
public record LoginRequest(

        @Schema(description = "Registered email address", example = "arjun@example.com")
        String email,

        @Schema(description = "Account password", example = "Password@123")
        String password

) {}