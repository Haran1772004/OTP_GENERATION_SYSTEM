package com.otp.otpservice.service;

import com.otp.common.event.OtpPurpose;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Stores active OTP hashes and verification attempt counts in Redis.
 *
 * @since 1.0.0
 */
@Component
public class OtpCache {

    private static final String CODE_PREFIX = "otp:code:";

    private static final String ATTEMPTS_PREFIX = "otp:attempts:";

    private final StringRedisTemplate redis;

    /**
     * Creates the cache.
     *
     * @param redis the Redis client
     */
    public OtpCache(StringRedisTemplate redis) {
        this.redis = redis;
    }

    /**
     * Stores a code hash and resets its attempt counter with the same TTL.
     *
     * @param userId  the user identifier
     * @param purpose why the code was requested
     * @param hash    the keyed hash of the code
     * @param ttl     how long both values remain available
     */
    public void store(long userId, OtpPurpose purpose, String hash,
            Duration ttl) {
        redis.opsForValue().set(codeKey(userId, purpose), hash, ttl);
        redis.opsForValue().set(attemptsKey(userId, purpose), "0", ttl);
    }

    /**
     * Finds the hash for a live code.
     *
     * @param userId  the user identifier
     * @param purpose why the code was requested
     * @return the stored hash, or empty when no code is present
     */
    public Optional<String> findHash(long userId, OtpPurpose purpose) {
        return Optional.ofNullable(
                redis.opsForValue().get(codeKey(userId, purpose)));
    }

    /**
     * Increments the wrong-try counter for a code.
     *
     * @param userId  the user identifier
     * @param purpose why the code was requested
     * @return the updated attempt count
     * @throws IllegalStateException if Redis does not return a count
     */
    public long incrementAttempts(long userId, OtpPurpose purpose) {
        Long attempts = redis.opsForValue()
                .increment(attemptsKey(userId, purpose));
        if (attempts == null) {
            throw new IllegalStateException(
                    "Redis did not return an OTP attempt count");
        }
        return attempts;
    }

    /**
     * Removes the code hash and attempt counter.
     *
     * @param userId  the user identifier
     * @param purpose why the code was requested
     */
    public void clear(long userId, OtpPurpose purpose) {
        redis.delete(List.of(codeKey(userId, purpose),
                attemptsKey(userId, purpose)));
    }

    private String codeKey(long userId, OtpPurpose purpose) {
        return CODE_PREFIX + userId + ":" + purpose;
    }

    private String attemptsKey(long userId, OtpPurpose purpose) {
        return ATTEMPTS_PREFIX + userId + ":" + purpose;
    }
}
