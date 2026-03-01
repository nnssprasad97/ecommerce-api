package com.nnssp.ecommerce_backend.controller;

import com.nnssp.ecommerce_backend.dto.ProductRequest;
import com.nnssp.ecommerce_backend.dto.ProductResponse;
import com.nnssp.ecommerce_backend.entity.Product;
import com.nnssp.ecommerce_backend.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<ProductResponse> getAllProducts(
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String order) {
        return productService.getAllProducts(sortBy, order).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/category/{category}")
    public List<ProductResponse> getByCategory(@PathVariable String category) {
        return productService.getProductsByCategory(category).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable Long id) {
        return mapToResponse(productService.getProductById(id));
    }

    // Admin only (In real life, secure this!)
    @PostMapping
    public ProductResponse createProduct(@Valid @RequestBody ProductRequest request) {
        Product product = mapToEntity(request);
        return mapToResponse(productService.saveProduct(product));
    }

    @PutMapping("/{id}")
    public ProductResponse updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        Product existingProduct = productService.getProductById(id);
        existingProduct.setName(request.getName());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setStockQuantity(request.getStockQuantity());
        existingProduct.setCategory(request.getCategory());
        return mapToResponse(productService.saveProduct(existingProduct));
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    private ProductResponse mapToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        response.setStockQuantity(product.getStockQuantity());
        response.setCategory(product.getCategory());
        return response;
    }

    private Product mapToEntity(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(request.getCategory());
        return product;
    }
}