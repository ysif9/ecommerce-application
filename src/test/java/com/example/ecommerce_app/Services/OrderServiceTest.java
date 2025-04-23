package com.example.ecommerce_app.Services;

import com.example.ecommerce_app.Model.CartItem;
import com.example.ecommerce_app.Model.LocalUser;
import com.example.ecommerce_app.Model.Product;
import com.example.ecommerce_app.Model.UserOrder;
import com.example.ecommerce_app.Repositories.OrderItemRepository;
import com.example.ecommerce_app.Repositories.UserOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@AutoConfigureMockMvc
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
