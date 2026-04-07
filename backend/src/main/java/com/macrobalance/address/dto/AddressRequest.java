package com.macrobalance.address.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressRequest(

        @NotBlank
        @Size(max = 255)
        String line1,

        @Size(max = 255)
        String line2,

        @NotBlank
        @Size(max = 100)
        String city,

        @NotBlank
        @Size(max = 100)
        String state,

        @NotBlank
        @Pattern(regexp = "^[1-9][0-9]{5}$",
                message = "Invalid Indian postal code")
        String postalCode,

        @Size(max = 50)
        String label,       // "Home", "Work", "Other"

        boolean isDefault
) {}