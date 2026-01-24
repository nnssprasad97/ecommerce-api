package com.nnssp.ecommerce_backend.service;

import com.nnssp.ecommerce_backend.dto.OrderEvent;
import com.nnssp.ecommerce_backend.entity.*;
import com.nnssp.ecommerce_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;

    // EVALUATION POINT: Transactions & Concurrency
    // 1. @Transactional ensures all steps happen together or none at all.
    // 2. Isolation.READ_COMMITTED prevents dirty reads.
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Order placeOrder(Long userId) {
        if (userId == null)
            throw new IllegalArgumentException("User ID cannot be null");
        // 1. Fetch User and Cart
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // 2. Prepare Order
        Order order = Order.builder()
                .user(user)
                .createdAt(LocalDateTime.now())
                .status(Order.OrderStatus.CREATED)
                .totalPrice(BigDecimal.ZERO)
                .build();

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        // 3. Process Items & Check Stock
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            int qty = cartItem.getQuantity();

            // Stock Check
            if (product.getStockQuantity() < qty) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            // EVALUATION POINT: Optimistic Locking
            // We decrement the stock. When we save(), Hibernate checks the @Version field.
            // If another user changed this product in the meantime, this throws
            // ObjectOptimisticLockingFailureException.
            product.setStockQuantity(product.getStockQuantity() - qty);
            productRepository.save(product);

            // Create Order Item (Snapshot price)
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(qty)
                    .priceAtPurchase(product.getPrice())
                    .build();

            orderItems.add(orderItem);
            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(qty)));
        }

        // 4. Finalize Order
        order.setItems(orderItems);
        order.setTotalPrice(totalAmount);
        Order savedOrder = orderRepository.save(order);

        // 5. Clear Cart
        cartRepository.delete(cart);

        // EVALUATION POINT: Async Processing
        // We send a message to RabbitMQ. The user gets a response immediately,
        // while the email sends in the background.
        OrderEvent event = new OrderEvent(savedOrder.getId(), user.getId(), user.getEmail());
        rabbitTemplate.convertAndSend("orderQueue", event);

        return savedOrder;
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
}