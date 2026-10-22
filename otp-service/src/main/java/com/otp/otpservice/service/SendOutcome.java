package com.otp.otpservice.service;

/**
 * Result of a request to send a code.
 *
 * @since 1.0.0
 */
public enum SendOutcome {

    /** A code was created and handed over for delivery. */
    SENT,

    /** The recipient asked for too many codes in a short time. */
    RATE_LIMITED
}
