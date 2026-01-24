package com.nnssp.ecommerce_backend.controller;

import com.nnssp.ecommerce_backend.dto.OrderItemResponse;
import com.nnssp.ecommerce_backend.dto.OrderResponse;
import com.nnssp.ecommerce_backend.entity.Order;
import com.nnssp.ecommerce_backend.entity.OrderItem;
import com.nnssp.ecommerce_backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/{userId}")
    public OrderResponse placeOrder(@PathVariable Long userId) {
        // This calls the Transactional Service we wrote earlier
        return mapToResponse(orderService.placeOrder(userId));
    }

    @GetMapping("/{id}")
    public OrderResponse getOrderById(@PathVariable Long id) {
        return mapToResponse(orderService.getOrderById(id));
    }

    private OrderResponse mapToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUser().getId());
        response.setTotalPrice(order.getTotalPrice());
        response.setCreatedAt(order.getCreatedAt());
        response.setStatus(order.getStatus().name());
        response.setItems(order.getItems().stream()
                .map(this::mapItemToResponse)
                .collect(Collectors.toList()));
        return response;
    }

    private OrderItemResponse mapItemToResponse(OrderItem item) {
        OrderItemResponse response = new OrderItemResponse();
        response.setId(item.getId());
        response.setProductId(item.getProduct().getId());
        response.setProductName(item.getProduct().getName());
        response.setQuantity(item.getQuantity());
        response.setPriceAtPurchase(item.getPriceAtPurchase());
        return response;
    }
}