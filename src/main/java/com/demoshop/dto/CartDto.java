package com.demoshop.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public class CartDto {

    public record CartResponse(Long id, List<CartItemResponse> items, BigDecimal total) {}

    public record CartItemResponse(
            Long id, Long productId, String productName,
            BigDecimal price, Integer quantity, BigDecimal subtotal
    ) {}

    public record AddItemRequest(
            @NotNull Long productId,
            @NotNull @Min(1) Integer quantity
    ) {}

    public record UpdateItemRequest(
            @NotNull @Min(1) Integer quantity
    ) {}
}
