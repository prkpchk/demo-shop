package com.demoshop.controller;

import com.demoshop.domain.User;
import com.demoshop.dto.CartDto;
import com.demoshop.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartDto.CartResponse> getCart(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(cartService.getCart(user));
    }

    @PostMapping("/items")
    public ResponseEntity<CartDto.CartResponse> addItem(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CartDto.AddItemRequest req) {
        return ResponseEntity.ok(cartService.addItem(user, req));
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<CartDto.CartResponse> updateItem(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody CartDto.UpdateItemRequest req) {
        return ResponseEntity.ok(cartService.updateItem(user, id, req));
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<CartDto.CartResponse> removeItem(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        return ResponseEntity.ok(cartService.removeItem(user, id));
    }
}
