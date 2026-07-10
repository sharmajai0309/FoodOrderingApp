package com.deliveryService.service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic orderConfirmedTopic() {
        return TopicBuilder.name("order.confirmed")
                // Configured with 3 partitions to allow up to 3 consumer instances in the future
                .partitions(3)
                .replicas(1)
                .build();
    }
}
