package com.nnssp.ecommerce_backend.dto;

import lombok.Data;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class ProductRequest {
    @NotEmpty(message = "Product name cannot be empty")
    private String name;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    @Min(value = 0, message = "Stock quantity cannot be negative")
    private int stockQuantity;

    @NotEmpty(message = "Category cannot be empty")
    private String category;
}
