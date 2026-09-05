package com.surya.orderservice.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long productId,
        String productName,
        BigDecimal unitPrice,
        int quantity
) {
}