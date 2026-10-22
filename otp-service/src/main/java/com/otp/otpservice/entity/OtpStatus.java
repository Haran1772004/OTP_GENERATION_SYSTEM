package com.otp.otpservice.entity;

/**
 * Lifecycle states of a stored one-time password.
 *
 * @since 1.0.0
 */
public enum OtpStatus {

    /** The code can still be verified. */
    PENDING,

    /** The code was verified successfully. */
    VERIFIED,

    /** The code expired without being verified. */
    EXPIRED,

    /** The code was locked after too many failed attempts. */
    FAILED
}
