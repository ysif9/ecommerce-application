package com.example.ecommerce_app.Repositories;

import com.example.ecommerce_app.Model.Cart;
import com.example.ecommerce_app.Model.LocalUser;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface CartRepository extends ListCrudRepository<Cart, Long> {
    Optional<Cart> findByUser(LocalUser user);

}
