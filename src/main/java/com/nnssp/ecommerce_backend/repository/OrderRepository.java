package com.nnssp.ecommerce_backend.repository;

import com.nnssp.ecommerce_backend.entity.Order;
import com.nnssp.ecommerce_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}