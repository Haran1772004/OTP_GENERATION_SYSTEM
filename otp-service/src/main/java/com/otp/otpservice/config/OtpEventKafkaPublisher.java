package com.otp.otpservice.config;

import com.otp.common.event.OtpEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Forwards OTP application events to the configured Kafka topic.
 *
 * @since 1.0.0
 */
@Component
public class OtpEventKafkaPublisher {

    private final KafkaTemplate<String, OtpEvent> kafkaTemplate;

    private final OtpProperties properties;

    /**
     * Creates the publisher.
     *
     * @param kafkaTemplate the Kafka producer
     * @param properties    the OTP settings containing the topic
     */
    public OtpEventKafkaPublisher(KafkaTemplate<String, OtpEvent> kafkaTemplate,
            OtpProperties properties) {
        this.kafkaTemplate = kafkaTemplate;
        this.properties = properties;
    }

    /**
     * Publishes an OTP event after the OTP service raises it locally.
     *
     * @param event the OTP event to publish
     */
    @EventListener
    public void publish(OtpEvent event) {
        kafkaTemplate.send(properties.getKafkaTopic(),
                Long.toString(event.userId()), event);
    }
}
