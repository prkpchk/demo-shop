package com.demoshop.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UserDto {

    public record UserResponse(
            Long id, String email, String name, String role,
            BigDecimal balance, LocalDateTime createdAt
    ) {}

    public record UpdateRequest(
            @NotBlank String name,
            @Email String email
    ) {}

    public record TopUpRequest(
            @NotNull @DecimalMin("0.01") BigDecimal amount
    ) {}
}
