package com.surya.inventoryservice.event;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductEvent(
        String eventType,
        Long productId,
        String name,
        BigDecimal price,
        Integer quantity,
        Instant occurredAt
) {
}