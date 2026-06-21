package com.demoshop.service;

import com.demoshop.domain.Product;
import com.demoshop.dto.ProductDto;
import com.demoshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Page<ProductDto.ProductResponse> getAll(String category, Pageable pageable) {
        if (category != null && !category.isBlank()) {
            return productRepository.findByCategory(category, pageable).map(this::toResponse);
        }
        return productRepository.findAll(pageable).map(this::toResponse);
    }

    public ProductDto.ProductResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public ProductDto.ProductResponse create(ProductDto.CreateRequest req) {
        Product p = new Product();
        p.setName(req.name());
        p.setDescription(req.description());
        p.setPrice(req.price());
        p.setStock(req.stock());
        p.setImageUrl(req.imageUrl());
        p.setCategory(req.category());
        return toResponse(productRepository.save(p));
    }

    @Transactional
    public ProductDto.ProductResponse update(Long id, ProductDto.UpdateRequest req) {
        Product p = findOrThrow(id);
        p.setName(req.name());
        p.setDescription(req.description());
        p.setPrice(req.price());
        p.setStock(req.stock());
        p.setImageUrl(req.imageUrl());
        p.setCategory(req.category());
        return toResponse(productRepository.save(p));
    }

    @Transactional
    public void delete(Long id) {
        productRepository.delete(findOrThrow(id));
    }

    private Product findOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Product not found: " + id));
    }

    public ProductDto.ProductResponse toResponse(Product p) {
        return new ProductDto.ProductResponse(p.getId(), p.getName(), p.getDescription(),
                p.getPrice(), p.getStock(), p.getImageUrl(), p.getCategory(), p.getCreatedAt());
    }
}
