package com.otp.otpservice.service;

import com.otp.common.event.OtpPurpose;
import com.otp.otpservice.config.OtpProperties;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Service;

/**
 * Generates one-time passwords and computes keyed hashes for storage.
 *
 * @since 1.0.0
 */
@Service
public class OtpCodeService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final SecureRandom random = new SecureRandom();

    private final OtpProperties properties;

    private final SecretKeySpec key;

    /**
     * Creates the service.
     *
     * @param properties OTP settings containing the code length and secret
     */
    public OtpCodeService(OtpProperties properties) {
        this.properties = properties;
        String secret = properties.hmacSecret();
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException(
                    "OTP HMAC secret must not be blank");
        }
        key = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
    }

    /**
     * Generates a random numeric code with the configured number of digits.
     *
     * @return the generated code, including any leading zeroes
     */
    public String generate() {
        int length = properties.codeLength();
        if (length <= 0) {
            throw new IllegalStateException(
                    "OTP code length must be greater than zero");
        }

        StringBuilder code = new StringBuilder(length);
        for (int index = 0; index < length; index++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    /**
     * Computes a keyed hash of a code bound to its user and purpose.
     *
     * @param userId  the user identifier
     * @param purpose why the code was requested
     * @param code    the plain-text code
     * @return the keyed hash as a lowercase hexadecimal string
     */
    public String hash(long userId, OtpPurpose purpose, String code) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(key);
            String input = userId + ":" + purpose + ":" + code;
            byte[] digest = mac.doFinal(
                    input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException(
                    "HMAC-SHA256 is not available", exception);
        }
    }

    /**
     * Checks whether a plain-text code matches a stored keyed hash.
     *
     * @param expectedHash the stored hash
     * @param userId       the user identifier
     * @param purpose      why the code was requested
     * @param code         the code entered by the user
     * @return {@code true} when the code matches the stored hash
     */
    public boolean matches(String expectedHash, long userId,
            OtpPurpose purpose, String code) {
        byte[] expected = expectedHash.getBytes(StandardCharsets.US_ASCII);
        byte[] actual = hash(userId, purpose, code)
                .getBytes(StandardCharsets.US_ASCII);
        return MessageDigest.isEqual(expected, actual);
    }
}
