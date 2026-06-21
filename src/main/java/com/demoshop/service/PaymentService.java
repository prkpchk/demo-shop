package com.demoshop.service;

import com.demoshop.domain.Order;
import com.demoshop.domain.User;
import com.demoshop.dto.OrderDto;
import com.demoshop.kafka.OrderEvent;
import com.demoshop.kafka.OrderEventProducer;
import com.demoshop.repository.OrderRepository;
import com.demoshop.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderEventProducer eventProducer;

    @Transactional
    public OrderDto.OrderResponse pay(User user, Long orderId, boolean simulateFailure) {
        Order order = orderRepository.findByIdAndUser(orderId, user)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));
        if (order.getStatus() != Order.Status.PENDING) {
            throw new IllegalStateException("Order is not in PENDING status");
        }
        if (simulateFailure) {
            order.setStatus(Order.Status.CANCELLED);
            user.setBalance(user.getBalance().add(order.getTotalAmount()));
            userRepository.save(user);
            eventProducer.send(OrderEvent.of("PAYMENT_FAILED", order.getId(), user.getId(),
                    user.getEmail(), order.getTotalAmount(), order.getStatus().name()));
        } else {
            order.setStatus(Order.Status.PAID);
            eventProducer.send(OrderEvent.of("PAYMENT_SUCCESS", order.getId(), user.getId(),
                    user.getEmail(), order.getTotalAmount(), order.getStatus().name()));
        }
        Order saved = orderRepository.save(order);
        return toResponse(saved);
    }

    private OrderDto.OrderResponse toResponse(Order order) {
        List<OrderDto.OrderItemResponse> items = order.getItems().stream()
                .map(i -> new OrderDto.OrderItemResponse(
                        i.getId(), i.getProduct().getId(), i.getProduct().getName(),
                        i.getQuantity(), i.getPrice(),
                        i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity()))))
                .toList();
        return new OrderDto.OrderResponse(order.getId(), order.getTotalAmount(),
                order.getStatus().name(), order.getCreatedAt(), items);
    }
}
