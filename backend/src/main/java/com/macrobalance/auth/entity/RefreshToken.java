package com.macrobalance.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * JPA entity representing a JWT refresh token.
 *
 * <p>Refresh tokens are long-lived credentials that allow users to
 * obtain new JWT access tokens without re-authenticating. Each token
 * is unique and tied to a specific user.
 *
 * <p><strong>Note:</strong> The refresh token flow is defined in the
 * schema but the {@code POST /api/auth/refresh} endpoint has not yet
 * been implemented. This entity is ready for that feature.
 *
 * <p>Tokens should be revoked ({@code revoked = true}) on logout
 * rather than deleted, to maintain an audit trail.
 */
@Entity
@Getter
@Setter
@Table(name = "refresh_tokens")
public class RefreshToken {

    /**
     * Primary key, auto-incremented.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Foreign key to the user this refresh token belongs to.
     * Stored as a plain Long to avoid a bidirectional relationship
     * with the User entity.
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * The refresh token string — a unique opaque value.
     * Must be kept secret by the client (stored in HttpOnly cookie
     * or secure storage, never in localStorage).
     */
    @Column(nullable = false, unique = true)
    private String token;

    /**
     * Timestamp after which this refresh token is no longer valid.
     * Typically set to 7 days from creation.
     */
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    /**
     * Whether this token has been explicitly revoked.
     * Set to {@code true} on logout or when a new refresh token is issued.
     * Revoked tokens must be rejected even if they have not yet expired.
     */
    private boolean revoked = false;

    /**
     * Timestamp when this token was created.
     * Immutable after insert.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}