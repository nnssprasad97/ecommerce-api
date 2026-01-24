package com.nnssp.ecommerce_backend.controller;

import com.nnssp.ecommerce_backend.dto.CartRequest;
import com.nnssp.ecommerce_backend.entity.Cart;
import com.nnssp.ecommerce_backend.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public Cart addToCart(@Valid @RequestBody CartRequest request) {
        return cartService.addToCart(request.getUserId(), request.getProductId(), request.getQuantity());
    }

    @GetMapping
    public Cart getCart(@RequestParam Long userId) {
        return cartService.getCart(userId);
    }

    @DeleteMapping("/items/{id}")
    public void removeFromCart(@RequestParam Long userId, @PathVariable Long id) {
        cartService.removeFromCart(userId, id);
    }
}