package com.demoshop.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductDto {

    public record ProductResponse(
            Long id, String name, String description, BigDecimal price,
            Integer stock, String imageUrl, String category, LocalDateTime createdAt
    ) {}

    public record CreateRequest(
            @NotBlank String name,
            String description,
            @NotNull @DecimalMin("0.01") BigDecimal price,
            @NotNull @Min(0) Integer stock,
            String imageUrl,
            String category
    ) {}

    public record UpdateRequest(
            @NotBlank String name,
            String description,
            @NotNull @DecimalMin("0.01") BigDecimal price,
            @NotNull @Min(0) Integer stock,
            String imageUrl,
            String category
    ) {}
}
