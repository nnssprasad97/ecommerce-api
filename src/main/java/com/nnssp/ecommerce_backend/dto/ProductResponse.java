package com.nnssp.ecommerce_backend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private int stockQuantity;
    private String category;
}
