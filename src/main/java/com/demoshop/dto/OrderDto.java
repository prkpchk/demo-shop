package com.demoshop.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {

    public record OrderResponse(
            Long id, BigDecimal totalAmount, String status,
            LocalDateTime createdAt, List<OrderItemResponse> items
    ) {}

    public record OrderItemResponse(
            Long id, Long productId, String productName,
            Integer quantity, BigDecimal price, BigDecimal subtotal
    ) {}
}
