package com.example.ecommerce_app;

import com.example.ecommerce_app.Model.*;
import com.example.ecommerce_app.Repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final CartRepository cartRepository;
    private final UserRepository localUserRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserOrderRepository userOrderRepository;
    private final OrderItemRepository orderItemRepository;

    public DataLoader(CartRepository cartRepository, UserRepository localUserRepository, ProductRepository productRepository, PasswordEncoder passwordEncoder, UserOrderRepository userOrderRepository, OrderItemRepository orderItemRepository) {
        this.cartRepository = cartRepository;
        this.localUserRepository = localUserRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
        this.userOrderRepository = userOrderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public void run(String... args) {
        // Create sample user
        LocalUser myuser = new LocalUser(
                "username",
                passwordEncoder.encode("password"),
                "email@example.com",
                "John",
                "Doe",
                "123 Main St",
                LocalDateTime.now(),
                "USER",
                "1234567890"
        );
        localUserRepository.save(myuser);

        // Create cart for user
        cartRepository.save(new Cart(myuser, null));

        // Create sample products
        List<Product> products = Arrays.asList(
                new Product("Laptop", 999.99, 10, "High-performance laptop with 16GB RAM", "https://example.com/laptop.jpg", "Electronics"),
                new Product("Smartphone", 699.99, 15, "Latest smartphone with 5G capability", "https://example.com/phone.jpg", "Electronics"),
                new Product("Headphones", 199.99, 20, "Noise-cancelling wireless headphones", "https://example.com/headphones.jpg", "Electronics"),
                new Product("Coffee Maker", 89.99, 8, "Programmable coffee maker with thermal carafe", "https://example.com/coffee.jpg", "Home Appliances"),
                new Product("Running Shoes", 129.99, 12, "Lightweight running shoes with cushioning", "https://example.com/shoes.jpg", "Sports"),
                new Product("Backpack", 79.99, 25, "Water-resistant backpack with laptop compartment", "https://example.com/backpack.jpg", "Accessories"),
                new Product("Smart Watch", 249.99, 5, "Fitness tracker with heart rate monitor", "https://example.com/watch.jpg", "Electronics"),
                new Product("Desk Chair", 199.99, 6, "Ergonomic office chair with lumbar support", "https://example.com/chair.jpg", "Furniture"),
                new Product("Blender", 59.99, 10, "High-speed blender for smoothies and more", "https://example.com/blender.jpg", "Home Appliances"),
                new Product("Wireless Mouse", 49.99, 30, "Ergonomic wireless mouse with long battery life", "https://example.com/mouse.jpg", "Electronics")
        );

        UserOrder userOrder = new UserOrder();
        userOrder.setUser(myuser);
        userOrder.setOrderDate(LocalDateTime.now());
        userOrder.setTotalPrice(1000.0);
        userOrder.setStatus("PENDING");

        OrderItem orderItem = new OrderItem();
        orderItem.setProductName("Laptop");
        orderItem.setQuantity(1);
        orderItem.setPrice(999.99);

        userOrder.setItems(
                Arrays.asList(
                        orderItem
                )
        );
        orderItem.setOrder(userOrder);



        // Save all products
        productRepository.saveAll(products);

        userOrderRepository.save(userOrder);

    }
}
