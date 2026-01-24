package com.nnssp.ecommerce_backend.service;

import com.nnssp.ecommerce_backend.entity.Cart;
import com.nnssp.ecommerce_backend.entity.CartItem;
import com.nnssp.ecommerce_backend.entity.Product;
import com.nnssp.ecommerce_backend.entity.User;
import com.nnssp.ecommerce_backend.repository.CartRepository;
import com.nnssp.ecommerce_backend.repository.ProductRepository;
import com.nnssp.ecommerce_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public Cart addToCart(Long userId, Long productId, int quantity) {
        if (userId == null)
            throw new IllegalArgumentException("User ID cannot be null");
        if (productId == null)
            throw new IllegalArgumentException("Product ID cannot be null");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user).orElse(new Cart());
        if (cart.getUser() == null) {
            cart.setUser(user);
            cart.setItems(new ArrayList<>());
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if item already exists in cart
        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cart.getItems().add(newItem);
        }

        return cartRepository.save(cart);
    }

    public Cart getCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found for user"));
    }

    @Transactional
    public void removeFromCart(Long userId, Long cartItemId) {
        Cart cart = getCart(userId);
        boolean removed = cart.getItems().removeIf(item -> item.getId().equals(cartItemId));
        if (!removed) {
            throw new RuntimeException("Item not found in cart");
        }
        cartRepository.save(cart);
    }
}
