package com.demoshop.controller;

import com.demoshop.domain.User;
import com.demoshop.dto.OrderDto;
import com.demoshop.service.OrderService;
import com.demoshop.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<OrderDto.OrderResponse> placeOrder(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.placeOrder(user));
    }

    @GetMapping
    public ResponseEntity<List<OrderDto.OrderResponse>> getOrders(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(orderService.getOrders(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto.OrderResponse> getOrder(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrder(user, id));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<OrderDto.OrderResponse> pay(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean simulateFailure) {
        return ResponseEntity.ok(paymentService.pay(user, id, simulateFailure));
    }
}
