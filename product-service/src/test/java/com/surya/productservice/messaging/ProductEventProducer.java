package com.surya.productservice.messaging;

import com.surya.productservice.event.ProductEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class ProductEventProducer {

    private static final String TOPIC = "product-events";

    private final KafkaTemplate<String, ProductEvent> kafkaTemplate;

    public ProductEventProducer(
            KafkaTemplate<String, ProductEvent> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;
    }

    public CompletableFuture<SendResult<String, ProductEvent>>
    publish(ProductEvent event) {

        return kafkaTemplate.send(
                TOPIC,
                event.productId().toString(),
                event
        );
    }
}
