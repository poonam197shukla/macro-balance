package com.macrobalance.auth.entity;

/**
 * Enum representing the delivery channel for a one-time password.
 *
 * <ul>
 *   <li>{@link #EMAIL} — OTP delivered via email using JavaMailSender</li>
 *   <li>{@link #PHONE} — OTP delivered via SMS (currently logged to console
 *       in development; integrate an SMS provider for production)</li>
 * </ul>
 */
public enum OtpType {

    /** OTP sent to the user's email address. */
    EMAIL,

    /** OTP sent to the user's phone number via SMS. */
    PHONE
}