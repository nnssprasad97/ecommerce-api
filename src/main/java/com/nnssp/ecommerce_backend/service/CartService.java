package com.nnssp.ecommerce_backend.service;

import com.nnssp.ecommerce_backend.entity.Cart;
import com.nnssp.ecommerce_backend.entity.CartItem;
import com.nnssp.ecommerce_backend.entity.Product;
import com.nnssp.ecommerce_backend.entity.User;
import com.nnssp.ecommerce_backend.exception.ResourceNotFoundException;
import com.nnssp.ecommerce_backend.repository.CartRepository;
import com.nnssp.ecommerce_backend.repository.ProductRepository;
import com.nnssp.ecommerce_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public Cart addToCart(Long userId, Long productId, Integer quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Cart cart = cartRepository.findByUser(user).orElse(new Cart());
        if (cart.getUser() == null) {
            cart.setUser(user);
            cart.setItems(new ArrayList<>());
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(quantity);

        cart.getItems().add(item);

        return cartRepository.save(cart);
    }

    public Cart getCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
    }

    public void removeFromCart(Long userId, Long itemId) {
        Cart cart = getCart(userId);

        boolean removed = cart.getItems().removeIf(item -> item.getId().equals(itemId));
        if (!removed) {
            throw new ResourceNotFoundException("Item not found in cart");
        }

        cartRepository.save(cart);
    }
}
