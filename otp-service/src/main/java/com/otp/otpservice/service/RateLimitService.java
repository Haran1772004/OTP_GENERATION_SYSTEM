package com.otp.otpservice.service;

import com.otp.common.event.OtpChannel;
import com.otp.otpservice.config.OtpProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Limits how many codes a recipient can request in a time window.
 *
 * @since 1.0.0
 */
@Service
public class RateLimitService {

    private static final String KEY_PREFIX = "otp:rate:";

    private final StringRedisTemplate redis;

    private final OtpProperties properties;

    /**
     * Creates the service.
     *
     * @param redis      the Redis client
     * @param properties the OTP settings holding the limit
     */
    public RateLimitService(StringRedisTemplate redis,
            OtpProperties properties) {
        this.redis = redis;
        this.properties = properties;
    }

    /**
     * Counts one request and tells whether it is still within the limit.
     *
     * @param channel   the delivery channel
     * @param recipient the email address or phone number
     * @return {@code true} if the request is allowed
     */
    public boolean isAllowed(OtpChannel channel, String recipient) {
        String key = KEY_PREFIX + channel + ":" + recipient;
        Long count = redis.opsForValue().increment(key);
        if (count == null) {
            return false;
        }
        if (count == 1L) {
            redis.expire(key, properties.rateLimit().window());
        }
        return count <= properties.rateLimit().maxRequests();
    }
}