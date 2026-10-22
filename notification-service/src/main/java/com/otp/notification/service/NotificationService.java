package com.otp.notification.service;

import com.otp.common.event.OtpEvent;

import org.springframework.stereotype.Service;

/**
 * Sends each OTP event to the service that handles its channel.
 *
 * @since 1.0.0
 */
@Service
public class NotificationService {

    private final SmsService smsService;

    private final EmailService emailService;

    /**
     * Creates the service.
     *
     * @param smsService the service that sends SMS messages
     * @param emailService the service that sends email messages
     */
    public NotificationService(SmsService smsService,
            EmailService emailService) {
        this.smsService = smsService;
        this.emailService = emailService;
    }

    /**
     * Sends the message for the given event through its channel.
     *
     * @param event the event to deliver
     */
    public void send(OtpEvent event) {
        switch (event.channel()) {
            case SMS -> smsService.send(event);
            case EMAIL -> emailService.send(event);
        }
    }
}
