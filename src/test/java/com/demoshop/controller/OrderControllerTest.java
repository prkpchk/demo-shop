package com.demoshop.controller;

import com.demoshop.config.SecurityConfig;
import com.demoshop.domain.User;
import com.demoshop.dto.OrderDto;
import com.demoshop.repository.UserRepository;
import com.demoshop.service.OrderService;
import com.demoshop.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@Import(SecurityConfig.class)
class OrderControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean OrderService orderService;
    @MockBean PaymentService paymentService;
    @MockBean UserRepository userRepository;

    private User user;
    private Authentication auth;
    private OrderDto.OrderResponse sampleOrder;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@demo.shop");
        user.setRole(User.Role.USER);
        auth = new UsernamePasswordAuthenticationToken(
                user, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        sampleOrder = new OrderDto.OrderResponse(1L, new BigDecimal("100.00"),
                "PENDING", LocalDateTime.now(), List.of());
    }

    @Test
    void placeOrder_returns201() throws Exception {
        when(orderService.placeOrder(user)).thenReturn(sampleOrder);

        mockMvc.perform(post("/api/v1/orders").with(authentication(auth)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void getOrders_returnsListForUser() throws Exception {
        when(orderService.getOrders(user)).thenReturn(List.of(sampleOrder));

        mockMvc.perform(get("/api/v1/orders").with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void pay_returns200WithSimulateFailureFalse() throws Exception {
        OrderDto.OrderResponse paid = new OrderDto.OrderResponse(1L, new BigDecimal("100.00"),
                "PAID", LocalDateTime.now(), List.of());
        when(paymentService.pay(user, 1L, false)).thenReturn(paid);

        mockMvc.perform(post("/api/v1/orders/1/pay")
                        .param("simulateFailure", "false")
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));
    }

    @Test
    void pay_returns200WithSimulateFailureTrue() throws Exception {
        OrderDto.OrderResponse cancelled = new OrderDto.OrderResponse(1L, new BigDecimal("100.00"),
                "CANCELLED", LocalDateTime.now(), List.of());
        when(paymentService.pay(user, 1L, true)).thenReturn(cancelled);

        mockMvc.perform(post("/api/v1/orders/1/pay")
                        .param("simulateFailure", "true")
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }
}
