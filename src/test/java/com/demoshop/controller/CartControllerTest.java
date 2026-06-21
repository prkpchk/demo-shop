package com.demoshop.controller;

import com.demoshop.config.SecurityConfig;
import com.demoshop.domain.User;
import com.demoshop.dto.CartDto;
import com.demoshop.repository.UserRepository;
import com.demoshop.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
@Import(SecurityConfig.class)
class CartControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean CartService cartService;
    @MockBean UserRepository userRepository;

    private User user;
    private Authentication auth;
    private CartDto.CartResponse emptyCart;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@demo.shop");
        user.setRole(User.Role.USER);
        auth = new UsernamePasswordAuthenticationToken(
                user, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        emptyCart = new CartDto.CartResponse(1L, List.of(), BigDecimal.ZERO);
    }

    @Test
    void getCart_returnsCartForAuthenticatedUser() throws Exception {
        when(cartService.getCart(user)).thenReturn(emptyCart);

        mockMvc.perform(get("/api/v1/cart").with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getCart_returns401WithoutAuth() throws Exception {
        mockMvc.perform(get("/api/v1/cart"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void addItem_returnsUpdatedCart() throws Exception {
        CartDto.CartItemResponse item = new CartDto.CartItemResponse(
                1L, 1L, "Laptop", new BigDecimal("999.99"), 1, new BigDecimal("999.99"));
        CartDto.CartResponse cartWithItem = new CartDto.CartResponse(
                1L, List.of(item), new BigDecimal("999.99"));
        when(cartService.addItem(eq(user), any(CartDto.AddItemRequest.class))).thenReturn(cartWithItem);

        mockMvc.perform(post("/api/v1/cart/items")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CartDto.AddItemRequest(1L, 1))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].productName").value("Laptop"));
    }

    @Test
    void removeItem_returnsUpdatedCart() throws Exception {
        when(cartService.removeItem(eq(user), eq(1L))).thenReturn(emptyCart);

        mockMvc.perform(delete("/api/v1/cart/items/1").with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty());
    }
}
