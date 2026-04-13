package com.macrobalance.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * JPA entity representing an OTP generation request.
 *
 * <p>Each time a user requests an OTP, a new record is created.
 * The OTP itself is never stored in plaintext — it is hashed with
 * BCrypt before persistence.
 *
 * <p>Stale records are cleaned up by {@code OtpCleanupScheduler}
 * which runs every hour and deletes records older than 1 hour.
 */
@Entity
@Getter
@Setter
@Table(name = "otp_requests")
public class OtpRequest {

    /** Primary key, auto-incremented. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Foreign key to the user who requested the OTP.
     * Nullable because OTPs can be requested before a user account
     * exists (e.g., during registration flows).
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * Email address the OTP was sent to.
     * Populated when {@code type} is {@link OtpType#EMAIL}.
     */
    private String email;

    /**
     * Phone number the OTP was sent to.
     * Populated when {@code type} is {@link OtpType#PHONE}.
     */
    private String phone;

    /**
     * Delivery channel for this OTP — either EMAIL or PHONE.
     * Stored as a string in the database.
     */
    @Enumerated(EnumType.STRING)
    private OtpType type;

    /**
     * BCrypt hash of the original 6-digit OTP.
     * The plaintext OTP is never persisted.
     */
    @Column(name = "hashed_otp", nullable = false)
    private String hashedOtp;

    /**
     * Number of failed validation attempts for this OTP.
     * Once this reaches the maximum (3), the OTP is locked
     * and further attempts are rejected even if the OTP is correct.
     */
    private Integer attempts = 0;

    /**
     * Timestamp after which this OTP is no longer valid.
     * Set to 5 minutes from creation time.
     */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /**
     * Timestamp when this OTP request was created.
     * Immutable after insert.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}