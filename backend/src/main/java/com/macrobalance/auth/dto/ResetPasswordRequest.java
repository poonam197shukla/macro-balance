package com.macrobalance.auth.dto;

public record ResetPasswordRequest(String identifier, String otp, String newPassword) {
}