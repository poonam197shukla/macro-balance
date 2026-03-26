package com.macrobalance.auth.dto;

public record VerifyOtpRequest(String identifier, String otp) {
}
