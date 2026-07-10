package com.Food.config;

import com.Food.kafka.DriverAssignedEvent;
import com.Food.kafka.OrderDeliveredEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka Consumer Config for the food service.
 *
 * Uses a SEPARATE group "food-service-group" from the delivery service's
 * "delivery-service-group" so both services can independently consume shared topics.
 *
 * Two topics consumed:
 *   - "driver.assigned"  → DriverAssignedEvent
 *   - "order.delivered"  → OrderDeliveredEvent
 */
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    private Map<String, Object> baseConsumerProps(String groupId) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }

    // ── Generic container factory with StringJsonMessageConverter ────────────

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> deliveryKafkaListenerContainerFactory() {
        ConsumerFactory<String, String> factory = new DefaultKafkaConsumerFactory<>(
                baseConsumerProps("food-service-group")
        );

        ConcurrentKafkaListenerContainerFactory<String, String> containerFactory =
                new ConcurrentKafkaListenerContainerFactory<>();
        containerFactory.setConsumerFactory(factory);
        
        org.springframework.kafka.support.converter.StringJsonMessageConverter converter = new org.springframework.kafka.support.converter.StringJsonMessageConverter();
        org.springframework.kafka.support.mapping.DefaultJackson2JavaTypeMapper typeMapper = new org.springframework.kafka.support.mapping.DefaultJackson2JavaTypeMapper();
        typeMapper.setTypePrecedence(org.springframework.kafka.support.mapping.Jackson2JavaTypeMapper.TypePrecedence.INFERRED);
        typeMapper.addTrustedPackages("*");
        converter.setTypeMapper(typeMapper);
        
        containerFactory.setRecordMessageConverter(converter);
        containerFactory.setConcurrency(1);
        return containerFactory;
    }
}
