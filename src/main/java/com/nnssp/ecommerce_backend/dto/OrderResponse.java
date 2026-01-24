package com.nnssp.ecommerce_backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private Long userId;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
    private String status;
    private List<OrderItemResponse> items;
}
