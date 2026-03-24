package com.macrobalance.user.dto;

public record RegisterRequest(
        String name,
        String email,
        String phone,
        String password
) {
}