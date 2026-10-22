package com.otp.common.event;

/**
 * Delivery channel used to send a one-time password to a user.
 *
 * @since 1.0.0
 */
public enum OtpChannel {

    /** The code is sent as a text message to the user's phone number. */
    SMS,

    /** The code is sent to the user's email address. */
    EMAIL
}
