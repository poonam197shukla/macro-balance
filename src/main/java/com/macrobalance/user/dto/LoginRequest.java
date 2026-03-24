package com.macrobalance.user.dto;

public record LoginRequest(
        String email,
        String password
) {
}