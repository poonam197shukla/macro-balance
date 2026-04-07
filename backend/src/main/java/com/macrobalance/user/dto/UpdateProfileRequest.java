package com.macrobalance.user.dto;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(

        @Size(min = 2, max = 150)
        String name,

        @Size(max = 20)
        String phone
) {}