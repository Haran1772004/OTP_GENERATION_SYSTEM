package com.otp.otpservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Entry point of the OTP service.
 *
 * <p>The service generates, stores and verifies one-time passwords and
 * publishes an event for each code that has to be delivered to a user.
 *
 * @since 1.0.0
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class OtpServiceApplication {

    /**
     * Starts the OTP service.
     *
     * @param args command line arguments passed to Spring Boot
     */
    public static void main(String[] args) {
        SpringApplication.run(OtpServiceApplication.class, args);
    }
}
