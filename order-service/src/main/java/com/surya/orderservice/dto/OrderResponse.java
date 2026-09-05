package com.surya.orderservice.dto;

import com.surya.orderservice.model.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        String customerName,
        String customerEmail,
        BigDecimal totalAmount,
        OrderStatus status,
        Instant createdAt,
        List<OrderItemResponse> items
) {
}