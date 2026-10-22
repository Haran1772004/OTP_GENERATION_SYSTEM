package com.otp.notification.listener;

import com.otp.common.event.OtpEvent;
import com.otp.notification.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Listens for OTP events on Kafka.
 *
 * @since 1.0.0
 */
@Component
public class OtpEventListener {

    private final NotificationService notificationService;

    /**
     * Creates the listener.
     *
     * @param notificationService the service that delivers each event
     */
    public OtpEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Handles one OTP event read from the topic.
     *
     * @param event the received event
     */
    @KafkaListener(topics = "${otp.kafka.topic}")
    public void onEvent(OtpEvent event) {
        notificationService.send(event);
    }
}
