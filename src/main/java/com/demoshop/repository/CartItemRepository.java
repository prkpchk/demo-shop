package com.demoshop.repository;

import com.demoshop.domain.Cart;
import com.demoshop.domain.CartItem;
import com.demoshop.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}
