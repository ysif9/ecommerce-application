package com.example.ecommerce_app.Repositories;

import com.example.ecommerce_app.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(String category);
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);

    Optional<Product> findByNameIgnoreCase(String name);

    Optional<Product> findByProductID(@NonNull long productID);

    long deleteByProductID(long productID);
}
