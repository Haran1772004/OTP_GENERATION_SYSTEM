package com.otp.otpservice.dto;

/**
 * Response body that carries a short message.
 *
 * @param message the message for the caller
 * @since 1.0.0
 */
public record MessageResponse(String message) {
}
