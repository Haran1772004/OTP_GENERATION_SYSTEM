package com.otp.otpservice.service;

import com.otp.common.event.OtpChannel;
import com.otp.otpservice.exception.InvalidRecipientException;
import java.util.Locale;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

/**
 * Validates and normalizes email addresses and phone numbers.
 *
 * @since 1.0.0
 */
@Component
public class RecipientValidator {

    private static final Pattern EMAIL =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private static final Pattern PHONE = Pattern.compile("^\\+?[0-9]{10,15}$");

    /**
     * Validates the recipient for the given channel and normalizes it.
     *
     * @param channel   the delivery channel
     * @param recipient the email address or phone number
     * @return a normalized recipient
     * @throws InvalidRecipientException if the channel or recipient is invalid
     */
    public String normalize(OtpChannel channel, String recipient) {
        if (channel == null || recipient == null || recipient.isBlank()) {
            throw new InvalidRecipientException("Recipient is required");
        }

        String value = recipient.trim();
        if (channel == OtpChannel.EMAIL) {
            value = value.toLowerCase(Locale.ROOT);
            if (!EMAIL.matcher(value).matches()) {
                throw new InvalidRecipientException("Invalid email address");
            }
            return value;
        }

        value = value.replace(" ", "");
        if (!PHONE.matcher(value).matches()) {
            throw new InvalidRecipientException("Invalid phone number");
        }
        return value;
    }
}
