package com.demoshop.service;

import com.demoshop.domain.*;
import com.demoshop.dto.CartDto;
import com.demoshop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartDto.CartResponse getCart(User user) {
        Cart cart = getOrCreateCart(user);
        return toResponse(cart);
    }

    @Transactional
    public CartDto.CartResponse addItem(User user, CartDto.AddItemRequest req) {
        Cart cart = getOrCreateCart(user);
        Product product = productRepository.findById(req.productId())
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Product not found"));
        if (product.getStock() < req.quantity()) {
            throw new IllegalStateException("Not enough stock");
        }
        cartItemRepository.findByCartAndProduct(cart, product).ifPresentOrElse(
                item -> item.setQuantity(item.getQuantity() + req.quantity()),
                () -> {
                    CartItem item = new CartItem();
                    item.setCart(cart);
                    item.setProduct(product);
                    item.setQuantity(req.quantity());
                    cart.getItems().add(item);
                }
        );
        return toResponse(cartRepository.save(cart));
    }

    @Transactional
    public CartDto.CartResponse updateItem(User user, Long itemId, CartDto.UpdateItemRequest req) {
        Cart cart = getOrCreateCart(user);
        CartItem item = findItemInCart(cart, itemId);
        item.setQuantity(req.quantity());
        return toResponse(cartRepository.save(cart));
    }

    @Transactional
    public CartDto.CartResponse removeItem(User user, Long itemId) {
        Cart cart = getOrCreateCart(user);
        CartItem item = findItemInCart(cart, itemId);
        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        return toResponse(cartRepository.save(cart));
    }

    @Transactional
    public void clearCart(User user) {
        Cart cart = getOrCreateCart(user);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart c = new Cart();
            c.setUser(user);
            return cartRepository.save(c);
        });
    }

    private CartItem findItemInCart(Cart cart, Long itemId) {
        return cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Cart item not found"));
    }

    private CartDto.CartResponse toResponse(Cart cart) {
        List<CartDto.CartItemResponse> items = cart.getItems().stream().map(i -> {
            BigDecimal subtotal = i.getProduct().getPrice().multiply(BigDecimal.valueOf(i.getQuantity()));
            return new CartDto.CartItemResponse(i.getId(), i.getProduct().getId(),
                    i.getProduct().getName(), i.getProduct().getPrice(), i.getQuantity(), subtotal);
        }).toList();
        BigDecimal total = items.stream().map(CartDto.CartItemResponse::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartDto.CartResponse(cart.getId(), items, total);
    }
}
