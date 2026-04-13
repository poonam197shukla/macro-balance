package com.macrobalance.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request payload for sending an OTP to a user's email or phone.
 *
 * <p>The identifier type is auto-detected:
 * values containing {@code @} are treated as email addresses,
 * all others as phone numbers.
 */
@Schema(description = "Request payload for sending an OTP")
public record SendOtpRequest(

        @Schema(
                description = "Email address or phone number to send the OTP to. " +
                        "Values containing '@' are treated as email, others as phone.",
                example = "arjun@example.com"
        )
        String identifier

) {}