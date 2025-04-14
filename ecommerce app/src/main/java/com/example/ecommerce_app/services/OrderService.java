package com.example.ecommerce_app.services;

import com.example.ecommerce_app.model.*;
import com.example.ecommerce_app.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final UserOrderRepository orderRepo;

    public OrderService(UserOrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    public List<UserOrder> getOrdersByUser(LocalUser user, String status) {
        return (status == null) ? orderRepo.findByUser(user) : orderRepo.findByUserAndStatus(user, status);
    }

    public UserOrder getOrderById(Long id) {
        return orderRepo.findById(id).orElse(null);
    }

    public UserOrder placeOrder(LocalUser user, List<CartItem> cartItems) {
        UserOrder order = new UserOrder();
        order.setUser(user);
        order.setItems(cartItems);
        order.setStatus("pending");
        order.setOrderDate(LocalDateTime.now());

        double total = cartItems.stream()
                .mapToDouble(item -> item.getQuantity() * item.getProduct().getPrice())
                .sum();
        order.setTotalPrice(total);

        return orderRepo.save(order);
    }

    public UserOrder updateOrder(UserOrder order) {
        return orderRepo.save(order);
    }

    public void deleteOrder(Long id) {
        orderRepo.deleteById(id);
    }
}
