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
                new Product("Laptop", 999.99, 10, "High-performance laptop with 16GB RAM", "https://images.unsplash.com/photo-1693206578613-144dd540b892", "Electronics"),
                new Product("Smartphone", 699.99, 15, "Latest smartphone with 5G capability", "https://images.unsplash.com/photo-1699265837122-7636e128b4b0", "Electronics"),
                new Product("Headphones", 199.99, 20, "Noise-cancelling wireless headphones", "https://images.unsplash.com/photo-1505740106531-4243f3831c78", "Electronics"),
                new Product("Coffee Maker", 89.99, 8, "Programmable coffee maker with thermal carafe", "https://images.unsplash.com/photo-1608354580875-30bd4168b351", "Home Appliances"),
                new Product("Running Shoes", 129.99, 12, "Lightweight running shoes with cushioning", "https://images.unsplash.com/photo-1575537302964-96cd47c06b1b", "Sports"),
                new Product("Backpack", 79.99, 25, "Water-resistant backpack with laptop compartment", "https://images.unsplash.com/photo-1667411424594-403300e5cc35", "Accessories"),
                new Product("Smart Watch", 249.99, 5, "Fitness tracker with heart rate monitor", "https://images.unsplash.com/photo-1523170335258-f5ed11844a49", "Electronics"),
                new Product("Desk Chair", 199.99, 6, "Ergonomic office chair with lumbar support", "https://images.unsplash.com/photo-1688578735352-9a6f2ac3b70a", "Furniture"),
                new Product("Blender", 59.99, 10, "High-speed blender for smoothies and more", "https://images.unsplash.com/photo-1622818426197-d54f85b88690", "Home Appliances"),
                new Product("Wireless Mouse", 49.99, 30, "Ergonomic wireless mouse with long battery life", "https://images.unsplash.com/photo-1739742473235-34a7bd9b8f87", "Electronics")
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
