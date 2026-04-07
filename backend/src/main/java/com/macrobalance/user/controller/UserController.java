package com.macrobalance.user.controller;

import com.macrobalance.common.dto.ApiResponse;
import com.macrobalance.user.dto.ChangePasswordRequest;
import com.macrobalance.user.dto.UpdateProfileRequest;
import com.macrobalance.user.dto.UserProfileResponse;
import com.macrobalance.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(new ApiResponse<>(
                true, "Profile fetched",
                userService.getProfile(userId)
        ));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @RequestBody @Valid UpdateProfileRequest request,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(new ApiResponse<>(
                true, "Profile updated",
                userService.updateProfile(userId, request)
        ));
    }

    @PatchMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestBody @Valid ChangePasswordRequest request,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        userService.changePassword(userId, request);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Password changed", null));
    }
}