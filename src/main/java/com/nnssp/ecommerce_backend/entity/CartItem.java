package com.nnssp.ecommerce_backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Table(name = "cart_items")
public class CartItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Cart cart;

    @ManyToOne
    private Product product;

    private Integer quantity;
}