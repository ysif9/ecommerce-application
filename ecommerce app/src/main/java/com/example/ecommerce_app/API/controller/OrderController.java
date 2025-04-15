package com.example.ecommerce_app.API.controller;

import com.example.ecommerce_app.model.*;
import com.example.ecommerce_app.services.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // GET /api/orders
    @GetMapping
    public List<UserOrder> getOrders(@RequestParam(required = false) String status) {
        LocalUser user = getCurrentUser(); // Replace with actual logic
        return orderService.getOrdersByUser(user, status);
    }

    // GET /api/orders/{id}
    @GetMapping("/{id}")
    public UserOrder getOrder(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    // POST /api/orders
    @PostMapping
    public UserOrder placeOrder() {
        LocalUser user = getCurrentUser();
        List<CartItem> cartItems = getCartItemsFromUser(user); // implement this logic
        return orderService.placeOrder(user, cartItems);
    }

    // PUT /api/orders/{id}
    @PutMapping("/{id}")
    public UserOrder updateOrder(@PathVariable Long id, @RequestBody UserOrder updatedOrder) {
        updatedOrder.setId(id);
        return orderService.updateOrder(updatedOrder);
    }

    // DELETE /api/orders/{id}
    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }

    private LocalUser getCurrentUser() {
        // TODO: replace with Spring Security or session logic
        return new LocalUser(); // placeholder
    }

    private List<CartItem> getCartItemsFromUser(LocalUser user) {
        // TODO: get the items from the Cart entity of this user
        return List.of(); // placeholder
    }
}
