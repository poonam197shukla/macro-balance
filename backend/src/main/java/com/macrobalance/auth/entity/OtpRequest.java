package com.macrobalance.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "otp_requests")
public class OtpRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    private String email;

    private String phone;

    @Enumerated(EnumType.STRING)
    private OtpType type;

    @Column(name = "hashed_otp", nullable = false)
    private String hashedOtp;

    private Integer attempts = 0;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
