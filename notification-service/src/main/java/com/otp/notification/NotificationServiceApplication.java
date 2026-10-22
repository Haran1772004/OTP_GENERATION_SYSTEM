package com.otp.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the notification service.
 *
 * <p>The service listens for OTP events on Kafka and delivers the message
 * to the user through the channel named in the event (SMS or email).
 *
 * @since 1.0.0
 */
@SpringBootApplication
public class NotificationServiceApplication {

    /**
     * Starts the notification service.
     *
     * @param args command line arguments passed to Spring Boot
     */
    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
