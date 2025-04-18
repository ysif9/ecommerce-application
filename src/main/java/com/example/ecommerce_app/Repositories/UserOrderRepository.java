package com.example.ecommerce_app.Repositories;

import com.example.ecommerce_app.Model.UserOrder;
import com.example.ecommerce_app.Model.LocalUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserOrderRepository extends JpaRepository<UserOrder, Long> {
    List<UserOrder> findByUser(LocalUser user);
    List<UserOrder> findByUserAndStatus(LocalUser user, String status);
}
