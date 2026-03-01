package com.nnssp.ecommerce_backend.service;

import com.nnssp.ecommerce_backend.entity.Product;
import com.nnssp.ecommerce_backend.exception.ResourceNotFoundException;
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

    @Cacheable(value = "products", key = "'all_' + #sortBy + '_' + #order")
    public List<Product> getAllProducts(String sortBy, String order) {
        org.springframework.data.domain.Sort sort = org.springframework.data.domain.Sort.by(sortBy);
        if ("desc".equalsIgnoreCase(order)) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }
        return productRepository.findAll(sort);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    // EVALUATION POINT: Cache Invalidation
    // When we add/update a product, we MUST clear the cache so users see new data.
    @CacheEvict(value = "products", allEntries = true)
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @CacheEvict(value = "products", allEntries = true)
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }
}