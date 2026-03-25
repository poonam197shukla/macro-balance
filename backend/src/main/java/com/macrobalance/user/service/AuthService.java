package com.macrobalance.user.service;

import com.macrobalance.security.jwt.JwtUtil;
import com.macrobalance.user.dto.LoginRequest;
import com.macrobalance.user.dto.RegisterRequest;
import com.macrobalance.user.entity.Role;
import com.macrobalance.user.entity.User;
import com.macrobalance.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    // 🔹 Register
    public String register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already registered");
        }

        if (request.phone() != null &&
                userRepository.existsByPhone(request.phone())) {
            throw new RuntimeException("Phone already registered");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);

        userRepository.save(user);

        return "User registered successfully";
    }

    // 🔹 Login
    public String login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow();

        return jwtUtil.generateToken(user.getId(), user.getEmail());
    }

}