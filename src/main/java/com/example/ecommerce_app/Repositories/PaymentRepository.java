package com.example.ecommerce_app.Repositories;

import com.example.ecommerce_app.Model.LocalUser;
import com.example.ecommerce_app.Model.Payment;
import com.example.ecommerce_app.Model.UserOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrder(UserOrder order);
    List<Payment> findByUser(LocalUser user);

    void deleteByOrder_OrderID(long orderOrderID);
}