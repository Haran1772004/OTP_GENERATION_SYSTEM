package com.otp.common.event;

/**
 * Event published by the OTP service when a code has to be delivered.
 *
 * <p>The notification service reads this event from Kafka and sends the
 * message through the given channel.
 *
 * @param userId           id of the user the code was issued for
 * @param channel          how the code is delivered
 * @param recipient        phone number or email address to deliver to
 * @param purpose          why the code was requested
 * @param otp              the one-time password in plain text
 * @param expiresInMinutes minutes until the code stops working
 * @since 1.0.0
 */
public record OtpEvent(
        long userId,
        OtpChannel channel,
        String recipient,
        OtpPurpose purpose,
        String otp,
        int expiresInMinutes) {
}
