package com.demoshop.service;

import com.demoshop.domain.*;
import com.demoshop.dto.OrderDto;
import com.demoshop.kafka.OrderEvent;
import com.demoshop.kafka.OrderEventProducer;
import com.demoshop.repository.OrderRepository;
import com.demoshop.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock OrderRepository orderRepository;
    @Mock UserRepository userRepository;
    @Mock OrderEventProducer eventProducer;
    @InjectMocks PaymentService paymentService;

    private User user;
    private Order order;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setBalance(new BigDecimal("400.00"));

        order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setTotalAmount(new BigDecimal("100.00"));
        order.setStatus(Order.Status.PENDING);
        order.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void pay_shouldSetPaidOnSuccess() {
        when(orderRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderDto.OrderResponse response = paymentService.pay(user, 1L, false);

        assertThat(response.status()).isEqualTo("PAID");
        verifyNoInteractions(userRepository);

        ArgumentCaptor<OrderEvent> captor = ArgumentCaptor.forClass(OrderEvent.class);
        verify(eventProducer).send(captor.capture());
        assertThat(captor.getValue().getEventType()).isEqualTo("PAYMENT_SUCCESS");
    }

    @Test
    void pay_shouldRefundBalanceAndCancelOnFailure() {
        when(orderRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        paymentService.pay(user, 1L, true);

        assertThat(order.getStatus()).isEqualTo(Order.Status.CANCELLED);
        assertThat(user.getBalance()).isEqualByComparingTo("500.00"); // 400 + 100 refund
        verify(userRepository).save(user);

        ArgumentCaptor<OrderEvent> captor = ArgumentCaptor.forClass(OrderEvent.class);
        verify(eventProducer).send(captor.capture());
        assertThat(captor.getValue().getEventType()).isEqualTo("PAYMENT_FAILED");
    }

    @Test
    void pay_shouldThrowWhenOrderNotFound() {
        when(orderRepository.findByIdAndUser(99L, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.pay(user, 99L, false))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Order not found");
    }

    @Test
    void pay_shouldThrowWhenOrderNotPending() {
        order.setStatus(Order.Status.PAID);
        when(orderRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> paymentService.pay(user, 1L, false))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Order is not in PENDING status");
    }
}
