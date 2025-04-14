package com.example.ecommerce_app.Repositories;

import com.example.ecommerce_app.Model.CartItem;
import org.springframework.data.repository.ListCrudRepository;

public interface CartItemRepository extends ListCrudRepository<CartItem, Long> {
}
