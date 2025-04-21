package com.example.ecommerce_app.services;

import com.example.ecommerce_app.model.*;
import com.example.ecommerce_app.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @Mock
    private UserOrderRepository orderRepo;

    @Mock
    private OrderItemRepository orderItemRepo;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetOrdersByUserWithoutStatus() {
        LocalUser user = new LocalUser();
        List<UserOrder> orders = List.of(new UserOrder());
        when(orderRepo.findByUser(user)).thenReturn(orders);

        List<UserOrder> result = orderService.getOrdersByUser(user, null);
        assertEquals(orders, result);
    }

    @Test
    public void testGetOrderById() {
        UserOrder order = new UserOrder();
        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));

        UserOrder result = orderService.getOrderById(1L);
        assertEquals(order, result);
    }

    @Test
    public void testPlaceOrder() {
        LocalUser user = new LocalUser();
        Product product = new Product();
        product.setName("Phone");
        product.setPrice(500.0);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(2);

        List<CartItem> cartItems = List.of(cartItem);

        when(orderRepo.save(any(UserOrder.class))).thenAnswer(i -> i.getArgument(0));

        UserOrder result = orderService.placeOrder(user, cartItems);

        assertNotNull(result);
        assertEquals(1000.0, result.getTotalPrice(), 0.01);
        assertEquals("pending", result.getStatus());
        assertEquals(user, result.getUser());
        assertNotNull(result.getItems());
    }

    @Test
    public void testUpdateOrder() {
        UserOrder order = new UserOrder();
        when(orderRepo.save(order)).thenReturn(order);
        assertEquals(order, orderService.updateOrder(order));
    }

    @Test
    public void testDeleteOrder() {
        Long id = 5L;
        orderService.deleteOrder(id);
        verify(orderRepo, times(1)).deleteById(id);
    }
}
