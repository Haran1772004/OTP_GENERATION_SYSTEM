package com.otp.otpservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Creates the Kafka topic used for OTP events.
 *
 * @since 1.0.0
 */
@Configuration
public class KafkaTopicConfig {

    /**
     * Declares the OTP events topic. Spring creates it on startup if it
     * does not exist.
     *
     * @param properties the OTP settings holding the topic name
     * @return the topic definition
     */
    @Bean
    public NewTopic otpEventsTopic(OtpProperties properties) {
        return TopicBuilder.name(properties.getKafkaTopic())
                .partitions(1)
                .replicas(1)
                .build();
    }
}
