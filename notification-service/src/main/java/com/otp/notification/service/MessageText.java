package com.otp.notification.service;

import com.otp.common.event.OtpEvent;
import java.util.Locale;

/**
 * Builds the text of OTP messages.
 *
 * @since 1.0.0
 */
final class MessageText {

    private MessageText() {
    }

    /**
     * Builds the message body.
     *
     * @param event the event holding the code and its expiry
     * @return the message body
     */
    static String body(OtpEvent event) {
        return String.format("Your %s OTP is %s. It expires in %d minutes.",
                purposeText(event), event.otp(), event.expiresInMinutes());
    }

    /**
     * Builds the email subject.
     *
     * @param event the event holding the purpose of the code
     * @return the email subject
     */
    static String subject(OtpEvent event) {
        return "Your " + purposeText(event) + " code";
    }

    private static String purposeText(OtpEvent event) {
        return event.purpose().name()
                .toLowerCase(Locale.ROOT).replace('_', ' ');
    }
}
