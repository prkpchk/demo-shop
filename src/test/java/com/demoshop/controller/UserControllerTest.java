package com.demoshop.controller;

import com.demoshop.config.SecurityConfig;
import com.demoshop.domain.User;
import com.demoshop.dto.UserDto;
import com.demoshop.repository.UserRepository;
import com.demoshop.service.UserService;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean UserService userService;
    @MockBean UserRepository userRepository;

    private User user;
    private Authentication auth;
    private UserDto.UserResponse sampleResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@demo.shop");
        user.setName("John Doe");
        user.setRole(User.Role.USER);
        user.setBalance(new BigDecimal("500.00"));
        auth = new UsernamePasswordAuthenticationToken(
                user, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        sampleResponse = new UserDto.UserResponse(1L, "user@demo.shop", "John Doe",
                "USER", new BigDecimal("500.00"), LocalDateTime.now());
    }

    @Test
    void getProfile_returnsCurrentUser() throws Exception {
        when(userService.getProfile(user)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/v1/users/me").with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@demo.shop"))
                .andExpect(jsonPath("$.balance").value(500.00));
    }

    @Test
    void topUp_returnsUpdatedBalance() throws Exception {
        UserDto.UserResponse updated = new UserDto.UserResponse(1L, "user@demo.shop", "John Doe",
                "USER", new BigDecimal("600.00"), LocalDateTime.now());
        when(userService.topUp(eq(user), any(UserDto.TopUpRequest.class))).thenReturn(updated);

        mockMvc.perform(post("/api/v1/users/me/top-up")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserDto.TopUpRequest(new BigDecimal("100.00")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(600.00));
    }

    @Test
    void getProfile_returns401WithoutAuth() throws Exception {
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isUnauthorized());
    }
}
