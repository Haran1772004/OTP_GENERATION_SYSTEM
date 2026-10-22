package com.otp.otpservice.service;

/**
 * Result of checking a code entered by a user.
 *
 * @since 1.0.0
 */
public enum VerifyOutcome {

    /** The code is correct. */
    VERIFIED,

    /** The code is wrong, and more tries are allowed. */
    INVALID_CODE,

    /** The code ran out of time. */
    EXPIRED,

    /** The code is wrong, and it is now locked. */
    TOO_MANY_ATTEMPTS,

    /** There is no active code for this recipient and purpose. */
    NOT_FOUND
}
