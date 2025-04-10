package com.example.ecommerce_app.Repositories;

import com.example.ecommerce_app.Model.Product;
import org.springframework.data.repository.ListCrudRepository;

public interface ProductRepository extends ListCrudRepository<Product, Long> {
}
