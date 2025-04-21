package com.example.ecommerce_app.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Entity
@Table(name = "User_order")
@NoArgsConstructor
public class UserOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OrderID", nullable = false, unique = true)
    private long orderID;

    @ManyToOne
    private LocalUser user;

    @OneToMany(cascade = CascadeType.ALL)
    private List<CartItem> items;

    private double totalPrice;

    private String status; // pending, completed, canceled, etc.

    private LocalDateTime orderDate;
}
