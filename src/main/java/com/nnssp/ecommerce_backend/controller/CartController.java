package com.nnssp.ecommerce_backend.controller;

import com.nnssp.ecommerce_backend.entity.*;
import com.nnssp.ecommerce_backend.repository.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @PostMapping("/add")
    public Cart addToCart(@RequestBody CartRequest request) {
        // 1. Find User
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Find or Create Cart
        Cart cart = cartRepository.findByUser(user).orElse(new Cart());
        if (cart.getUser() == null) {
            cart.setUser(user);
            cart.setItems(new ArrayList<>());
        }

        // 3. Find Product
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // 4. Add Item
        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(request.getQuantity());
        
        cart.getItems().add(item);
        
        return cartRepository.save(cart);
    }

    @Data
    public static class CartRequest {
        private Long userId;
        private Long productId;
        private Integer quantity;
    }
}