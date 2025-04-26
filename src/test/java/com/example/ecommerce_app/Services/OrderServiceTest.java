package com.example.ecommerce_app.Services;

import com.example.ecommerce_app.Model.*;
import com.example.ecommerce_app.Repositories.OrderItemRepository;
import com.example.ecommerce_app.Repositories.PaymentRepository;
import com.example.ecommerce_app.Repositories.UserOrderRepository;
import org.junit.jupiter.api.Assertions;
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
import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
public class OrderServiceTest {

    @Mock
    private UserOrderRepository orderRepo;

    @Mock
    private OrderItemRepository orderItemRepo;

    @InjectMocks
    private OrderService orderService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

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
        Assertions.assertFalse(orderRepo.existsById(id));
    }

    @Test
    public void testDeleteOrder_not_exist(){
        orderService.deleteOrder(1L);
        Assertions.assertFalse(orderRepo.existsById(1L));
    }

    @Test
    public void testDeleteOrder_with_payment() {
        // Arrange
        Long orderId = 10L;
        Payment payment = new Payment();
        UserOrder order = new UserOrder();
        order.setOrderID(orderId);
        payment.setOrder(order);

        // Mock payment service to return a payment
        when(paymentService.getPaymentByOrderId(orderId)).thenReturn(payment);
        when(orderRepo.existsById(orderId)).thenReturn(true);

        // Act
        orderService.deleteOrder(orderId);

        // Assert
        verify(paymentRepository).deleteByOrder_OrderID(orderId);
        verify(orderRepo).deleteById(orderId);
    }
    @Test
    public void testGetOrdersByUserWithStatus() {
        LocalUser user = new LocalUser();
        String status = "completed";
        List<UserOrder> orders = List.of(new UserOrder());
        when(orderRepo.findByUserAndStatus(user, status)).thenReturn(orders);

        List<UserOrder> result = orderService.getOrdersByUser(user, status);
        assertEquals(orders, result);
    }

}
