package com.demoshop.service;

import com.demoshop.domain.*;
import com.demoshop.dto.OrderDto;
import com.demoshop.kafka.OrderEvent;
import com.demoshop.kafka.OrderEventProducer;
import com.demoshop.repository.OrderRepository;
import com.demoshop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock CartService cartService;
    @Mock OrderRepository orderRepository;
    @Mock UserRepository userRepository;
    @Mock OrderEventProducer eventProducer;
    @InjectMocks OrderService orderService;

    private User user;
    private Cart cart;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setName("Test");
        user.setRole(User.Role.USER);
        user.setBalance(new BigDecimal("200.00"));
        user.setCreatedAt(LocalDateTime.now());

        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(new BigDecimal("50.00"));
        product.setStock(10);

        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);

        cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.getItems().add(cartItem);
    }

    @Test
    void placeOrder_shouldCreateOrderAndDeductBalance() {
        when(cartService.getOrCreateCart(user)).thenReturn(cart);
        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setUser(user);
        savedOrder.setTotalAmount(new BigDecimal("100.00"));
        savedOrder.setStatus(Order.Status.PENDING);
        savedOrder.setCreatedAt(LocalDateTime.now());
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        OrderDto.OrderResponse response = orderService.placeOrder(user);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.status()).isEqualTo("PENDING");
        assertThat(user.getBalance()).isEqualByComparingTo("100.00");
        verify(userRepository).save(user);
        verify(cartService).clearCart(user);

        ArgumentCaptor<OrderEvent> eventCaptor = ArgumentCaptor.forClass(OrderEvent.class);
        verify(eventProducer).send(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getEventType()).isEqualTo("ORDER_PLACED");
    }

    @Test
    void placeOrder_shouldThrowWhenCartIsEmpty() {
        cart.getItems().clear();
        when(cartService.getOrCreateCart(user)).thenReturn(cart);

        assertThatThrownBy(() -> orderService.placeOrder(user))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cart is empty");
    }

    @Test
    void placeOrder_shouldThrowWhenInsufficientBalance() {
        user.setBalance(new BigDecimal("50.00")); // cart total = 2 × 50 = 100
        when(cartService.getOrCreateCart(user)).thenReturn(cart);

        assertThatThrownBy(() -> orderService.placeOrder(user))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Insufficient balance");
    }

    @Test
    void getOrders_shouldReturnUserOrders() {
        Order order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setTotalAmount(new BigDecimal("100.00"));
        order.setStatus(Order.Status.PAID);
        order.setCreatedAt(LocalDateTime.now());
        when(orderRepository.findByUserOrderByCreatedAtDesc(user)).thenReturn(List.of(order));

        List<OrderDto.OrderResponse> result = orderService.getOrders(user);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).status()).isEqualTo("PAID");
    }
}
