package com.example.ecommerce_app;

import com.example.ecommerce_app.Repositories.CartRepository;
import com.example.ecommerce_app.Repositories.LocalUserRepository;
import com.example.ecommerce_app.Repositories.ProductRepository;
import com.example.ecommerce_app.Model.Cart;
import com.example.ecommerce_app.Model.LocalUser;
import com.example.ecommerce_app.Model.Product;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {

    private final CartRepository cartRepository;
    private final LocalUserRepository localUserRepository;
    private final ProductRepository productRepository;

    public DataLoader(CartRepository cartRepository, LocalUserRepository localUserRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.localUserRepository = localUserRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        LocalUser myuser = new LocalUser("username", "password", "email", "firstName", "lastName", "address", LocalDateTime.now(), "role", "phoneNumber");
        localUserRepository.save(myuser);
        cartRepository.save(new Cart(myuser, null));
        productRepository.save(new Product("Product 1", 100.0, 10, "Description", "ImageURL1", "Category1"));
    }
}
