package com.nnssp.ecommerce_backend.controller;

import com.nnssp.ecommerce_backend.entity.Order;
import com.nnssp.ecommerce_backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/{userId}")
    public Order placeOrder(@PathVariable Long userId) {
        // This calls the Transactional Service we wrote earlier
        return orderService.placeOrder(userId);
    }
}