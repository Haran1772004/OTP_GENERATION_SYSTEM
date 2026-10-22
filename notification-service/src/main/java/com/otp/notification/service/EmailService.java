package com.otp.notification.service;

import com.otp.common.event.OtpEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Sends OTP messages by email.
 *
 * <p>For now the message is only written to the log. A real mail client
 * can replace the body of {@link #send(OtpEvent)} later.
 *
 * @since 1.0.0
 */
@Service
public class EmailService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(EmailService.class);

    /**
     * Sends the OTP message to the email address in the event.
     *
     * @param event the event holding the email address and the code
     */
    public void send(OtpEvent event) {
        LOGGER.info("[EMAIL] To: {} | Subject: {} | {}",
                event.recipient(), MessageText.subject(event),
                MessageText.body(event));
    }
}
