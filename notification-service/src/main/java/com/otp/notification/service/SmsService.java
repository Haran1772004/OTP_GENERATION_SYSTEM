package com.otp.notification.service;

import com.otp.common.event.OtpEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Sends OTP messages by SMS.
 *
 * <p>For now the message is only written to the log. A real SMS provider
 * can replace the body of {@link #send(OtpEvent)} later.
 *
 * @since 1.0.0
 */
@Service
public class SmsService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(SmsService.class);

    /**
     * Sends the OTP message to the phone number in the event.
     *
     * @param event the event holding the phone number and the code
     */
    public void send(OtpEvent event) {
        LOGGER.info("[SMS] To: {} | {}",
                event.recipient(), MessageText.body(event));
    }
}
