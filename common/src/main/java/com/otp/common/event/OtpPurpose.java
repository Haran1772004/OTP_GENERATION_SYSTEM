package com.otp.common.event;

/**
 * Reason a one-time password was requested.
 *
 * <p>A code issued for one purpose must not be accepted for another.
 *
 * @since 1.0.0
 */
public enum OtpPurpose {

    /** The user is logging in. */
    LOGIN,

    /** The user is creating a new account. */
    SIGNUP,

    /** The user is resetting a forgotten password. */
    PASSWORD_RESET,

    /** The user is confirming a payment. */
    PAYMENT
}
