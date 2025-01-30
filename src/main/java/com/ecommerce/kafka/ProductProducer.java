package com.ecommerce.kafka;

import com.ecommerce.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@EnableKafka
public class ProductProducer {

    private static final String TOPIC = "products";

    @Autowired
    private KafkaTemplate<String, Product> kafkaTemplate;

    public void sendProduct(Product product) {
        kafkaTemplate.send(TOPIC, product);
    }
}
