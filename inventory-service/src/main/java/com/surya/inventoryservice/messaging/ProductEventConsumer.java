package com.surya.inventoryservice.messaging;

import com.surya.inventoryservice.event.ProductEvent;
import com.surya.inventoryservice.service.EventLogService;
import com.surya.inventoryservice.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProductEventConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(ProductEventConsumer.class);

    private final InventoryService inventoryService;
    private final EventLogService eventLogService;

    public ProductEventConsumer(
            InventoryService inventoryService,
            EventLogService eventLogService) {

        this.inventoryService = inventoryService;
        this.eventLogService = eventLogService;
    }

    @KafkaListener(topics = "product-events")
    public void consume(ProductEvent event) {
        inventoryService.processProductEvent(event);
        eventLogService.record(event);

        log.info(
                "{} event consumed: productId={}, name={}, quantity={}",
                event.eventType(),
                event.productId(),
                event.name(),
                event.quantity()
        );
    }
}