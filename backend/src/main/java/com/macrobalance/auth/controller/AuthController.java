package com.macrobalance.auth.controller;

import com.macrobalance.auth.dto.ResetPasswordRequest;
import com.macrobalance.auth.dto.SendOtpRequest;
import com.macrobalance.auth.dto.VerifyOtpRequest;
import com.macrobalance.auth.service.AuthService;
import com.macrobalance.common.dto.ApiResponse;
import com.macrobalance.user.dto.LoginRequest;
import com.macrobalance.user.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<String>> sendOtp(@RequestBody SendOtpRequest request) {
        authService.sendOtp(request.identifier());
        return ResponseEntity.ok(new ApiResponse<>(true, "OTP sent successfully", null));
    }

    @PostMapping("/login/otp")
    public ResponseEntity<ApiResponse<String>> loginWithOtp(@RequestBody VerifyOtpRequest request) {
        String token = authService.loginWithOtp(request.identifier(), request.otp());
        return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", token));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(
                request.identifier(),
                request.otp(),
                request.newPassword()
        );
        return ResponseEntity.ok(new ApiResponse<>(true, "Password reset successful", null));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "User registered successfully", authService.register(request))
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Login successful", authService.login(request))
        );
    }

}
