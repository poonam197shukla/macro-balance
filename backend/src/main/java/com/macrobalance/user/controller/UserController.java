package com.macrobalance.user.controller;

import com.macrobalance.common.dto.ApiResponse;
import com.macrobalance.user.dto.ChangePasswordRequest;
import com.macrobalance.user.dto.UpdateProfileRequest;
import com.macrobalance.user.dto.UserProfileResponse;
import com.macrobalance.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for the authenticated user's own profile.
 *
 * <p>All endpoints operate on the currently authenticated user —
 * there are no admin overrides here. Users can view their profile,
 * update their name and phone, and change their password.
 *
 * <p>All endpoints require a valid JWT token.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "View and manage the authenticated user's profile and password")
@SecurityRequirement(name = "Bearer Auth")
public class UserController {

    private final UserService userService;

    /**
     * Returns the profile of the currently authenticated user.
     *
     * @param authentication the current user's JWT context
     * @return user profile including name, email, phone, verification status, and role
     */
    @Operation(
            summary = "Get my profile",
            description = "Returns the profile of the currently authenticated user."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Profile fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Profile fetched",
                userService.getProfile(userId)
        ));
    }

    /**
     * Updates the name and/or phone number of the authenticated user.
     *
     * <p>Email and role cannot be changed through this endpoint.
     * If phone is updated, {@code isPhoneVerified} is reset to {@code false}.
     *
     * @param request        fields to update — name and/or phone
     * @param authentication the current user's JWT context
     * @return the updated user profile
     */
    @Operation(
            summary = "Update my profile",
            description = "Updates the authenticated user's name and/or phone number. " +
                    "Email and role cannot be changed. " +
                    "Updating phone resets isPhoneVerified to false."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Profile updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Validation failed or phone already in use"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @RequestBody @Valid UpdateProfileRequest request,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Profile updated",
                userService.updateProfile(userId, request)
        ));
    }

    /**
     * Changes the authenticated user's password.
     *
     * <p>The current password must be provided and verified before
     * the new password is applied. The new password is BCrypt-hashed
     * before storage.
     *
     * @param request        current password and the desired new password
     * @param authentication the current user's JWT context
     * @return empty success response
     */
    @Operation(
            summary = "Change my password",
            description = "Changes the authenticated user's password. " +
                    "Requires the current password for verification. " +
                    "New password must be at least 8 characters."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Password changed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Current password is incorrect or new password fails validation"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "Unauthorized")
    })
    @PatchMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestBody @Valid ChangePasswordRequest request,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        userService.changePassword(userId, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Password changed", null));
    }
}