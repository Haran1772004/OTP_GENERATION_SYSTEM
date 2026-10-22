package com.otp.otpservice.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Settings of the OTP service, read from the {@code otp} section of
 * application.yml.
 *
 * @since 1.0.0
 */
@Component
public class OtpProperties {

    private final int codeLength;

    private final Duration ttl;

    private final int maxVerifyAttempts;

    private final String hmacSecret;

    private final int rateLimitMaxRequests;

    private final Duration rateLimitWindow;

    private final String kafkaTopic;

    /**
     * Creates the settings from application.yml.
     *
     * @param codeLength           number of digits in a code
     * @param ttl                  how long a code stays valid
     * @param maxVerifyAttempts    wrong tries allowed before a code is locked
     * @param hmacSecret           secret key used to hash codes
     * @param rateLimitMaxRequests requests allowed inside the window
     * @param rateLimitWindow      length of the counting window
     * @param kafkaTopic           topic that receives the OTP events
     */
    public OtpProperties(
            @Value("${otp.code-length}") int codeLength,
            @Value("${otp.ttl}") Duration ttl,
            @Value("${otp.max-verify-attempts}") int maxVerifyAttempts,
            @Value("${otp.hmac-secret}") String hmacSecret,
            @Value("${otp.rate-limit.max-requests}") int rateLimitMaxRequests,
            @Value("${otp.rate-limit.window}") Duration rateLimitWindow,
            @Value("${otp.kafka.topic}") String kafkaTopic) {
        this.codeLength = codeLength;
        this.ttl = ttl;
        this.maxVerifyAttempts = maxVerifyAttempts;
        this.hmacSecret = hmacSecret;
        this.rateLimitMaxRequests = rateLimitMaxRequests;
        this.rateLimitWindow = rateLimitWindow;
        this.kafkaTopic = kafkaTopic;
    }

    /**
     * Returns the number of digits in a code.
     *
     * @return the code length
     */
    public int getCodeLength() {
        return codeLength;
    }

    /**
     * Returns how long a code stays valid.
     *
     * @return the code lifetime
     */
    public Duration getTtl() {
        return ttl;
    }

    /**
     * Returns the wrong tries allowed before a code is locked.
     *
     * @return the maximum verify attempts
     */
    public int getMaxVerifyAttempts() {
        return maxVerifyAttempts;
    }

    /**
     * Returns the secret key used to hash codes.
     *
     * @return the HMAC secret
     */
    public String getHmacSecret() {
        return hmacSecret;
    }

    /**
     * Returns the requests allowed inside the rate limit window.
     *
     * @return the maximum requests
     */
    public int getRateLimitMaxRequests() {
        return rateLimitMaxRequests;
    }

    /**
     * Returns the length of the rate limit window.
     *
     * @return the window length
     */
    public Duration getRateLimitWindow() {
        return rateLimitWindow;
    }

    /**
     * Returns the topic that receives the OTP events.
     *
     * @return the Kafka topic name
     */
    public String getKafkaTopic() {
        return kafkaTopic;
    }
}