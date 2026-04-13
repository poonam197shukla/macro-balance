package com.macrobalance.address.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response payload representing a saved delivery address.
 *
 * <p>Returned on all address read and write operations.
 * The {@code country} field always reflects the value stored
 * in the database (defaults to {@code "India"}).
 */
@Schema(description = "Saved delivery address belonging to the authenticated user")
public record AddressResponse(

        @Schema(description = "Unique identifier of the address", example = "1")
        Long id,

        @Schema(description = "Primary address line", example = "12, Koramangala 4th Block")
        String line1,

        @Schema(description = "Secondary address line", example = "Near Forum Mall")
        String line2,

        @Schema(description = "City", example = "Bengaluru")
        String city,

        @Schema(description = "State", example = "Karnataka")
        String state,

        @Schema(description = "6-digit postal code", example = "560034")
        String postalCode,

        @Schema(description = "Country — defaults to India", example = "India")
        String country,

        @Schema(description = "User-defined label", example = "Home")
        String label,

        @Schema(description = "Whether this is the user's default delivery address", example = "true")
        boolean isDefault

) {
}