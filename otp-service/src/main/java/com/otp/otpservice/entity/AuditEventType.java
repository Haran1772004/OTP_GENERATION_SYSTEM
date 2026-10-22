package com.otp.otpservice.entity;

/**
 * Describes an event recorded in the OTP audit log.
 *
 * @since 1.0.0
 */
public enum AuditEventType {

    /** A one-time password was created. */
    OTP_GENERATED,

    /** A one-time password was sent. */
    OTP_SENT,

    /** A one-time password was verified. */
    OTP_VERIFIED,

    /** Verification failed. */
    OTP_FAILED,

    /** A one-time password expired. */
    OTP_EXPIRED,

    /** A send request exceeded its rate limit. */
    OTP_RATE_LIMITED
}
