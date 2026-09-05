package com.surya.productservice.event;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductCreatedEvent(
        Long productId,
        String name,
        BigDecimal price,
        int quantity,
        Instant occurredAt
) {
}
