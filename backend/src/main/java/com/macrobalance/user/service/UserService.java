package com.macrobalance.user.service;

import com.macrobalance.common.exception.BadRequestException;
import com.macrobalance.user.dto.ChangePasswordRequest;
import com.macrobalance.user.dto.UpdateProfileRequest;
import com.macrobalance.user.dto.UserProfileResponse;
import com.macrobalance.user.entity.User;
import com.macrobalance.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserProfileResponse getProfile(Long userId) {
        User user = findUser(userId);
        return toResponse(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(Long userId,
                                             UpdateProfileRequest request) {
        User user = findUser(userId);

        // Only update fields that were actually provided
        if (request.name() != null && !request.name().isBlank()) {
            user.setName(request.name());
        }

        if (request.phone() != null && !request.phone().isBlank()) {

            // Make sure no other user has this phone
            userRepository.findByPhone(request.phone())
                    .filter(existing -> !existing.getId().equals(userId))
                    .ifPresent(__ -> {
                        throw new BadRequestException(
                                "Phone number already in use");
                    });

            // Phone changed — mark as unverified
            if (!request.phone().equals(user.getPhone())) {
                user.setPhoneVerified(false);
            }

            user.setPhone(request.phone());
        }

        return toResponse(userRepository.save(user));
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {

        User user = findUser(userId);

        if (!passwordEncoder.matches(
                request.currentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        if (request.currentPassword().equals(request.newPassword())) {
            throw new BadRequestException(
                    "New password must differ from current password");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    // ── Private ────────────────────────────────────────────────

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));
    }

    private UserProfileResponse toResponse(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.isEmailVerified(),
                user.isPhoneVerified(),
                user.getRole().name()
        );
    }
}