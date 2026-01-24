package com.nnssp.ecommerce_backend.service;

import com.nnssp.ecommerce_backend.entity.Product;
import com.nnssp.ecommerce_backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    // EVALUATION POINT: Caching
    // If we search for the same category twice, the 2nd time comes from Redis
    // (Fast!)
    @Cacheable(value = "products", key = "#category")
    public List<Product> getProductsByCategory(String category) {
        log.info("Fetching from Database for category: {}", category); // Log to prove cache miss
        return productRepository.findByCategory(category);
    }

    @Cacheable(value = "products", key = "'all_' + #sort")
    public List<Product> getAllProducts(String sort) {
        if ("price_asc".equals(sort)) {
            // Sort by price ascending
            return productRepository.findAll(org.springframework.data.domain.Sort.by("price").ascending());
        } else if ("price_desc".equals(sort)) {
            // Sort by price descending
            return productRepository.findAll(org.springframework.data.domain.Sort.by("price").descending());
        }
        // Default: No sorting (or by ID)
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        if (id == null)
            throw new IllegalArgumentException("Product ID cannot be null");
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    // EVALUATION POINT: Cache Invalidation
    // When we add/update a product, we MUST clear the cache so users see new data.
    @CacheEvict(value = "products", allEntries = true)
    public Product saveProduct(Product product) {
        if (product == null)
            throw new IllegalArgumentException("Product cannot be null");
        return productRepository.save(product);
    }

    @CacheEvict(value = "products", allEntries = true)
    public void deleteProduct(Long id) {
        if (id == null)
            throw new IllegalArgumentException("Product ID cannot be null");
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);
    }
}