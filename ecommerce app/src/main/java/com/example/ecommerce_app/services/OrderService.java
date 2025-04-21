package com.example.ecommerce_app.services;

import com.example.ecommerce_app.model.*;
import com.example.ecommerce_app.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final UserOrderRepository orderRepo;

    private final OrderItemRepository orderItemRepo;

    public OrderService(UserOrderRepository orderRepo, OrderItemRepository orderItemRepo) {
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
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
        order.setStatus("pending");
        order.setOrderDate(LocalDateTime.now());

        double total = 0.0;
        List<OrderItem> orderItems = new java.util.ArrayList<>();

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductName(cartItem.getProduct().getName());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            orderItem.setOrder(order); // link back to order

            total += cartItem.getQuantity() * cartItem.getProduct().getPrice();
            orderItems.add(orderItem);
        }

        order.setItems(orderItems); // now using OrderItem
        order.setTotalPrice(total);

        return orderRepo.save(order); // orderItems will be saved due to CascadeType.ALL
    }


    public UserOrder updateOrder(UserOrder order) {
        return orderRepo.save(order);
    }

    public void deleteOrder(Long id) {
        orderRepo.deleteById(id);
    }
}
