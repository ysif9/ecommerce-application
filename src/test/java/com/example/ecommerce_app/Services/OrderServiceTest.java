package com.example.ecommerce_app.Services;

import com.example.ecommerce_app.Model.OrderItem;
import com.example.ecommerce_app.Model.UserOrder;
import com.example.ecommerce_app.exception.OrderNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Test
    @Transactional
    @DisplayName("Test1: Place new order")
    public void placeOrder_success() {
        UserOrder order = new UserOrder();
        order.setOrderItems(Collections.singletonList(new OrderItem(1L, 2)));

        assertDoesNotThrow(() -> {
            orderService.placeOrder(order);
        });
    }

    @Test
    @Transactional
    @DisplayName("Test2: Get order by valid ID")
    public void getOrderById_success() {
        UserOrder order = orderService.getOrderById(1L);
        assertNotNull(order);
    }

    @Test
    @Transactional
    @DisplayName("Test3: Get order by invalid ID")
    public void getOrderById_fail() {
        assertThrows(OrderNotFoundException.class, () -> {
            orderService.getOrderById(999L);
        });
    }

    @Test
    @Transactional
    @DisplayName("Test4: Cancel order")
    public void cancelOrder_success() {
        assertDoesNotThrow(() -> orderService.cancelOrder(1L));
    }

    @Test
    @Transactional
    @DisplayName("Test5: List all orders")
    public void listOrders_success() {
        List<UserOrder> orders = orderService.getOrdersForUser("orderuser");
        assertNotNull(orders);
    }
}
