package com.demoshop.controller;

import com.demoshop.config.SecurityConfig;
import com.demoshop.domain.User;
import com.demoshop.dto.ProductDto;
import com.demoshop.repository.UserRepository;
import com.demoshop.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

@WebMvcTest(ProductController.class)
@Import(SecurityConfig.class)
class ProductControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean ProductService productService;
    @MockBean UserRepository userRepository; // required by JwtFilter

    private ProductDto.ProductResponse sample() {
        return new ProductDto.ProductResponse(1L, "Laptop", "Desc",
                new BigDecimal("999.99"), 10, null, "Electronics", LocalDateTime.now());
    }

    @Test
    void getAll_isPublicAndReturnsList() throws Exception {
        when(productService.getAll(isNull(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(sample())));

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Laptop"));
    }

    @Test
    void getById_isPublicAndReturnsProduct() throws Exception {
        when(productService.getById(1L)).thenReturn(sample());

        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void create_returns201ForAdmin() throws Exception {
        User admin = new User();
        admin.setId(1L);
        admin.setEmail("admin@demo.shop");
        admin.setRole(User.Role.ADMIN);
        when(productService.create(any())).thenReturn(sample());

        mockMvc.perform(post("/api/v1/products")
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                admin, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ProductDto.CreateRequest("Laptop", "Desc",
                                        new BigDecimal("999.99"), 10, null, "Electronics"))))
                .andExpect(status().isCreated());
    }

    @Test
    void create_returns403ForRegularUser() throws Exception {
        User regularUser = new User();
        regularUser.setId(2L);
        regularUser.setEmail("user@demo.shop");
        regularUser.setRole(User.Role.USER);

        mockMvc.perform(post("/api/v1/products")
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                regularUser, null, List.of(new SimpleGrantedAuthority("ROLE_USER")))))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ProductDto.CreateRequest("Laptop", "Desc",
                                        new BigDecimal("999.99"), 10, null, "Electronics"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void delete_returns204ForAdmin() throws Exception {
        User admin = new User();
        admin.setId(1L);
        admin.setRole(User.Role.ADMIN);

        mockMvc.perform(delete("/api/v1/products/1")
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                admin, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))))))
                .andExpect(status().isNoContent());
    }
}
