package com.otp.otpservice.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Settings of the OTP service, read from the {@code otp} section of
 * application.yml.
 *
 * @param codeLength        number of digits in a code
 * @param ttl               how long a code stays valid
 * @param maxVerifyAttempts wrong tries allowed before a code is locked
 * @param hmacSecret        secret key used to hash codes
 * @param rateLimit         limit on code requests per recipient
 * @param kafka             Kafka settings
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "otp")
public record OtpProperties(
        int codeLength,
        Duration ttl,
        int maxVerifyAttempts,
        String hmacSecret,
        RateLimit rateLimit,
        Kafka kafka) {

    /**
     * Limit on how often a recipient can request a code.
     *
     * @param maxRequests requests allowed inside the window
     * @param window      length of the counting window
     */
    public record RateLimit(int maxRequests, Duration window) {
    }

    /**
     * Kafka settings.
     *
     * @param topic topic that receives the OTP events
     */
    public record Kafka(String topic) {
    }
}
