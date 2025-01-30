package com.ecommerce.config;

import com.ecommerce.model.Product;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Bean
    //creates and configures a ProducerFactory for Kafka producers that will send messages of type Product.
    public ProducerFactory<String, Product> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // Ensure broker is reachable
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class); //key of the Kafka message will be a String
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class); // Serialize Product using JSON

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    //creates and configures a KafkaTemplate that is used for sending Product objects to a Kafka topic.
    public KafkaTemplate<String, Product> kafkaTemplate(ProducerFactory<String, Product> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
