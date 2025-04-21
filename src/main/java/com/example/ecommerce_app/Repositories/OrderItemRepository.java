package com.example.ecommerce_app.Repositories;

import com.example.ecommerce_app.Model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
