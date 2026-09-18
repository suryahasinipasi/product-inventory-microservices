package com.surya.productservice.messaging;

import com.surya.productservice.event.ProductEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class ProductEventProducer {

    private static final String TOPIC = "product-events";

    private final KafkaTemplate<String, ProductEvent> kafkaTemplate;
    private final boolean kafkaEnabled;
    private final boolean kafkaEnabled;

    public ProductEventProducer(
            KafkaTemplate<String, ProductEvent> kafkaTemplate,
            @Value("${app.kafka.enabled:true}") boolean kafkaEnabled) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaEnabled = kafkaEnabled;
    }

    public CompletableFuture<SendResult<String, ProductEvent>>
    publish(ProductEvent event) {
        if (!kafkaEnabled) {
            return CompletableFuture.completedFuture(null);
        }

        return kafkaTemplate.send(
                TOPIC,
                event.productId().toString(),
                event
        );
    }
}