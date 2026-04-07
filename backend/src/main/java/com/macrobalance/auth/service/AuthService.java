package com.macrobalance.auth.service;

import com.macrobalance.auth.entity.OtpType;
import com.macrobalance.common.exception.BadRequestException;
import com.macrobalance.security.jwt.JwtUtil;
import com.macrobalance.user.dto.LoginRequest;
import com.macrobalance.user.dto.RegisterRequest;
import com.macrobalance.user.entity.Role;
import com.macrobalance.user.entity.User;
import com.macrobalance.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final OtpService otpService;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public void sendOtp(String identifier) {

        OtpType type = identifier.contains("@") ? OtpType.EMAIL : OtpType.PHONE;

        String otp = otpService.generateOtp(identifier, type);

        if (type == OtpType.EMAIL)
            notificationService.sendEmailOtp(identifier, otp);
        else
            notificationService.sendSmsOtp(identifier, otp);
    }

    public String loginWithOtp(String identifier, String otp) {

        otpService.validateOtp(identifier, otp);

        User user = identifier.contains("@") ?
                userRepository.findByEmail(identifier).orElseThrow() :
                userRepository.findByPhone(identifier).orElseThrow();

        return jwtUtil.generateToken(user.getId(), user.getEmail(), String.valueOf(user.getRole()));
    }

    public void resetPassword(String identifier, String otp, String newPassword) {

        otpService.validateOtp(identifier, otp);

        User user = identifier.contains("@") ?
                userRepository.findByEmail(identifier).orElseThrow() :
                userRepository.findByPhone(identifier).orElseThrow();

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public String register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already registered");
        }

        if (request.phone() != null &&
                userRepository.existsByPhone(request.phone())) {
            throw new BadRequestException("Phone already registered");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);

        userRepository.save(user);

        return jwtUtil.generateToken(user.getId(), user.getEmail(), String.valueOf(user.getRole()));
    }

    public String login(LoginRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );
        } catch (Exception e) {
            throw new BadRequestException("Invalid credentials");
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow();

        return jwtUtil.generateToken(user.getId(), user.getEmail(), String.valueOf(user.getRole()));
    }

}
