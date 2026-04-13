package com.macrobalance.user.entity;

import com.macrobalance.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * JPA entity representing a registered user account.
 *
 * <p>Users authenticate via email + password or OTP. On successful
 * authentication, a JWT token is issued containing the user's ID,
 * email, and role as claims.
 *
 * <p>Verification flags ({@code isEmailVerified}, {@code isPhoneVerified})
 * are updated when the user completes an OTP flow for the respective channel.
 * These flags are informational — they do not currently gate access to any
 * feature, but are intended for future use (e.g. requiring email verification
 * before checkout).
 *
 * <p>Extends {@link BaseEntity} for {@code id}, audit timestamps,
 * {@code createdBy}, {@code updatedBy}, and optimistic locking via {@code version}.
 */
@Entity
@Getter
@Setter
@Table(name = "users")
public class User extends BaseEntity {

    /**
     * Full display name shown in the UI and on reviews.
     * Required. Not unique — multiple users may share a name.
     */
    @Column(nullable = false, length = 150)
    private String name;

    /**
     * Email address used for login and OTP delivery.
     * Must be unique across all accounts.
     * Cannot be changed after registration.
     */
    @Column(unique = true, nullable = false, length = 255)
    private String email;

    /**
     * Optional mobile phone number used for OTP delivery.
     * Must be unique if provided.
     * Can be updated via {@code PUT /api/users/me}, which resets
     * {@code isPhoneVerified} to {@code false}.
     */
    @Column(unique = true, length = 20)
    private String phone;

    /**
     * BCrypt-hashed password. The plaintext password is never stored.
     * Changed via {@code PATCH /api/users/me/password} (requires current password)
     * or reset via OTP at {@code POST /api/auth/reset-password}.
     * Never included in any API response.
     */
    @Column(nullable = false, length = 255)
    private String password;

    /**
     * Access role for this user.
     * Defaults to {@link Role#USER} on registration.
     * {@link Role#ADMIN} must be assigned manually.
     * Stored as a string in the database for readability.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    /**
     * Whether this user's email has been verified via OTP.
     * Set to {@code true} after a successful {@code POST /api/auth/login/otp}
     * using an email identifier.
     * Defaults to {@code false} on registration.
     */
    @Column(name = "is_email_verified")
    private boolean isEmailVerified = false;

    /**
     * Whether this user's phone number has been verified via OTP.
     * Set to {@code true} after a successful {@code POST /api/auth/login/otp}
     * using a phone identifier.
     * Reset to {@code false} if the phone number is changed via profile update.
     * Defaults to {@code false} on registration.
     */
    @Column(name = "is_phone_verified")
    private boolean isPhoneVerified = false;
}