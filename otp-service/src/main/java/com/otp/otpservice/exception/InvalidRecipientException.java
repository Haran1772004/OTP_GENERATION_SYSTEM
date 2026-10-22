package com.otp.otpservice.exception;

/**
 * Thrown when an email address or phone number is not valid.
 *
 * @since 1.0.0
 */
public class InvalidRecipientException extends RuntimeException {

    /**
     * Creates the exception.
     *
     * @param message what is wrong with the recipient
     */
    public InvalidRecipientException(String message) {
        super(message);
    }
}
