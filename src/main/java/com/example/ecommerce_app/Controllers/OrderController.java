package com.example.ecommerce_app.Controllers;

import com.example.ecommerce_app.Model.CartItem;
import com.example.ecommerce_app.Model.LocalUser;
import com.example.ecommerce_app.Model.UserOrder;
import com.example.ecommerce_app.Services.AuthService;
import com.example.ecommerce_app.Services.CartService;
import com.example.ecommerce_app.Services.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final AuthService authService;
    private final CartService cartService;

    public OrderController(OrderService orderService, AuthService authService, CartService cartService) {
        this.orderService = orderService;
        this.authService = authService;
        this.cartService = cartService;
    }

    // GET /api/orders
    @GetMapping
    public List<UserOrder> getOrders(Authentication authentication, @RequestParam(required = false) String status) {
        LocalUser user = authService.getUserFromAuthentication(authentication);
        return orderService.getOrdersByUser(user, status);
    }

    // GET /api/orders/{id}
    @GetMapping("/{id}")
    public UserOrder getOrder(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    // POST /api/orders
    @PostMapping
    public UserOrder placeOrder(Authentication authentication) {
        LocalUser user = authService.getUserFromAuthentication(authentication);
        List<CartItem> cartItems = getCartItemsFromUser(user); // implement this logic
        return orderService.placeOrder(user, cartItems);
    }

    // PUT /api/orders/{id}
    @PutMapping("/{id}")
    public UserOrder updateOrder(@PathVariable Long id, @RequestBody UserOrder updatedOrder) {
        updatedOrder.setOrderID(id);
        return orderService.updateOrder(updatedOrder);
    }

    // DELETE /api/orders/{id}
    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }


    private List<CartItem> getCartItemsFromUser(LocalUser user) {
        return cartService.getCartByUser(user).getItems();
    }
}
