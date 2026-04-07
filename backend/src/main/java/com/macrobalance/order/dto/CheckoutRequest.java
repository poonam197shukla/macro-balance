package com.macrobalance.order.dto;

import jakarta.validation.constraints.NotNull;

public record CheckoutRequest(

        @NotNull
        Long addressId,

        String notes
) {}