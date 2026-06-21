package com.demoshop.service;

import com.demoshop.domain.*;
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
public class OrderService {

    private final CartService cartService;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderEventProducer eventProducer;

    @Transactional
    public OrderDto.OrderResponse placeOrder(User user) {
        Cart cart = cartService.getOrCreateCart(user);
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }
        BigDecimal total = cart.getItems().stream()
                .map(i -> i.getProduct().getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (user.getBalance().compareTo(total) < 0) {
            throw new IllegalStateException("Insufficient balance");
        }
        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(total);
        order.setStatus(Order.Status.PENDING);
        cart.getItems().forEach(cartItem -> {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(cartItem.getProduct());
            item.setQuantity(cartItem.getQuantity());
            item.setPrice(cartItem.getProduct().getPrice());
            order.getItems().add(item);
        });
        Order saved = orderRepository.save(order);
        user.setBalance(user.getBalance().subtract(total));
        userRepository.save(user);
        cartService.clearCart(user);
        eventProducer.send(OrderEvent.of("ORDER_PLACED", saved.getId(), user.getId(),
                user.getEmail(), total, saved.getStatus().name()));
        return toResponse(saved);
    }

    public List<OrderDto.OrderResponse> getOrders(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user)
                .stream().map(this::toResponse).toList();
    }

    public OrderDto.OrderResponse getOrder(User user, Long orderId) {
        return toResponse(orderRepository.findByIdAndUser(orderId, user)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId)));
    }

    public OrderDto.OrderResponse toResponse(Order order) {
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
