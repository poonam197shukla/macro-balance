package com.macrobalance.user.dto;

public record UserProfileResponse(
        Long id,
        String name,
        String email,
        String phone,
        boolean isEmailVerified,
        boolean isPhoneVerified,
        String role
) {}