package com.example.ecommerce_app.Services;

import com.example.ecommerce_app.Model.CartItem;
import com.example.ecommerce_app.Model.LocalUser;
import com.example.ecommerce_app.Model.OrderItem;
import com.example.ecommerce_app.Model.UserOrder;
import com.example.ecommerce_app.Repositories.OrderItemRepository;
import com.example.ecommerce_app.Repositories.PaymentRepository;
import com.example.ecommerce_app.Repositories.UserOrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final UserOrderRepository orderRepo;

    private final OrderItemRepository orderItemRepo;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

    public OrderService(UserOrderRepository orderRepo, OrderItemRepository orderItemRepo, PaymentRepository paymentRepository, PaymentService paymentService) {
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
        this.paymentRepository = paymentRepository;
        this.paymentService = paymentService;
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

    @Transactional
    public void deleteOrder(Long id) {
        /// @check for any caused issues
//        paymentRepository.deleteByOrder_OrderID(id);
        orderRepo.deleteById(id);
    }
}
