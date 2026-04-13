package com.macrobalance.auth.controller;

import com.macrobalance.auth.dto.ResetPasswordRequest;
import com.macrobalance.auth.dto.SendOtpRequest;
import com.macrobalance.auth.dto.VerifyOtpRequest;
import com.macrobalance.auth.service.AuthService;
import com.macrobalance.common.dto.ApiResponse;
import com.macrobalance.user.dto.LoginRequest;
import com.macrobalance.user.dto.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller handling all authentication flows.
 *
 * <p>Supports two login methods:
 * <ul>
 *   <li>Email + password (standard login)</li>
 *   <li>OTP-based login via email or phone</li>
 * </ul>
 *
 * <p>All endpoints are public — no JWT required.
 * On successful login or registration, a JWT access token is returned
 * in the response body.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Registration, login, OTP and password reset endpoints")
public class AuthController {

    private final AuthService authService;

    /**
     * Sends a one-time password to the provided email address or phone number.
     *
     * <p>The identifier is auto-detected: values containing {@code @} are
     * treated as email addresses, all others as phone numbers.
     * The OTP expires after 5 minutes and allows a maximum of 3 validation attempts.
     *
     * @param request contains the email or phone identifier
     * @return success confirmation (OTP is delivered out-of-band)
     */
    @Operation(
            summary = "Send OTP",
            description = "Sends a 6-digit OTP to the provided email or phone number. " +
                    "OTP expires in 5 minutes and allows 3 attempts maximum."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "OTP sent successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Invalid identifier format")
    })
    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<String>> sendOtp(
            @RequestBody SendOtpRequest request) {

        authService.sendOtp(request.identifier());
        return ResponseEntity.ok(new ApiResponse<>(true, "OTP sent successfully", null));
    }

    /**
     * Authenticates a user using a previously sent OTP.
     *
     * <p>The identifier must match the one used when requesting the OTP.
     * On success, a signed JWT token is returned which must be included
     * as a {@code Bearer} token in subsequent authenticated requests.
     *
     * @param request contains the identifier and the OTP to verify
     * @return JWT access token on successful verification
     */
    @Operation(
            summary = "Login with OTP",
            description = "Verifies the OTP and returns a JWT access token. " +
                    "Use the returned token as 'Authorization: Bearer <token>' in subsequent requests."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Login successful — JWT token returned"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Invalid or expired OTP")
    })
    @PostMapping("/login/otp")
    public ResponseEntity<ApiResponse<String>> loginWithOtp(
            @RequestBody VerifyOtpRequest request) {

        String token = authService.loginWithOtp(request.identifier(), request.otp());
        return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", token));
    }

    /**
     * Resets the user's password using OTP verification.
     *
     * <p>The flow requires the user to first request an OTP via
     * {@code POST /api/auth/send-otp}, then call this endpoint with
     * the received OTP and the desired new password.
     *
     * @param request contains the identifier, OTP, and new password
     * @return success confirmation
     */
    @Operation(
            summary = "Reset password",
            description = "Resets the user's password after verifying their OTP. " +
                    "Call POST /api/auth/send-otp first to receive the OTP."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Password reset successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Invalid or expired OTP")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @RequestBody ResetPasswordRequest request) {

        authService.resetPassword(
                request.identifier(),
                request.otp(),
                request.newPassword()
        );
        return ResponseEntity.ok(new ApiResponse<>(true, "Password reset successful", null));
    }

    /**
     * Registers a new user account.
     *
     * <p>Email must be unique. Phone is optional but must be unique if provided.
     * The password is hashed with BCrypt before storage.
     * A JWT token is returned immediately on successful registration
     * so the user does not need to log in separately.
     *
     * @param request contains name, email, optional phone, and password
     * @return JWT access token for the newly registered user
     */
    @Operation(
            summary = "Register",
            description = "Creates a new user account. Returns a JWT token immediately " +
                    "so the user is logged in right after registration."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "User registered — JWT token returned"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Email or phone already registered")
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(
            @RequestBody RegisterRequest request) {

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "User registered successfully",
                authService.register(request)
        ));
    }

    /**
     * Authenticates a user with email and password.
     *
     * <p>On success, a signed JWT token is returned which must be included
     * as a {@code Bearer} token in subsequent authenticated requests.
     * The token is valid for 24 hours.
     *
     * @param request contains email and password
     * @return JWT access token on successful authentication
     */
    @Operation(
            summary = "Login with password",
            description = "Authenticates using email and password. Returns a JWT token valid for 24 hours. " +
                    "Use as 'Authorization: Bearer <token>' in subsequent requests."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Login successful — JWT token returned"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Invalid email or password")
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(
            @RequestBody LoginRequest request) {

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Login successful",
                authService.login(request)
        ));
    }
}