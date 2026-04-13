package com.macrobalance.address.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request payload for creating or updating a delivery address.
 *
 * <p>{@code line1}, {@code city}, {@code state}, and {@code postalCode}
 * are mandatory. {@code line2} and {@code label} are optional.
 */
@Schema(description = "Request payload for creating or updating a delivery address")
public record AddressRequest(

        @Schema(
                description = "Primary address line — building name, street, area",
                example = "12, Koramangala 4th Block"
        )
        @NotBlank
        @Size(max = 255)
        String line1,

        @Schema(
                description = "Secondary address line — landmark, floor, suite (optional)",
                example = "Near Forum Mall"
        )
        @Size(max = 255)
        String line2,

        @Schema(description = "City name", example = "Bengaluru")
        @NotBlank
        @Size(max = 100)
        String city,

        @Schema(description = "State name", example = "Karnataka")
        @NotBlank
        @Size(max = 100)
        String state,

        @Schema(
                description = "6-digit Indian postal code. Must not start with 0.",
                example = "560034"
        )
        @NotBlank
        @Pattern(
                regexp = "^[1-9][0-9]{5}$",
                message = "Invalid Indian postal code"
        )
        String postalCode,

        @Schema(
                description = "User-defined label for the address",
                example = "Home",
                allowableValues = {"Home", "Work", "Other"}
        )
        @Size(max = 50)
        String label,

        @Schema(
                description = "Whether this address should be set as the default delivery address",
                example = "true"
        )
        boolean isDefault

) {
}