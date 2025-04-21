package com.example.ecommerce_app.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "User_order")
public class UserOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OrderID", nullable = false, unique = true)
    private long orderID;
// identifier ID and relation with Cartitem
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private LocalUser user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

    private double totalPrice;

    private String status; // pending, completed, canceled, etc.

    private LocalDateTime orderDate;
// same getters and setters
    public LocalUser getUser() {
        return user;
    }
    public void setUser(LocalUser user) {
        this.user = user;
    }

    public long getOrderID() {
        return orderID;
    }
    public void setOrderID(long orderID) {
        this.orderID = orderID;
    }

    public void setItems(List<OrderItem> cartItems) {
    }

    public void setOrderDate(LocalDateTime now) {
    }

    public void setStatus(String pending) {
    }

    public void setTotalPrice(double total) {
    }

    public void setId(Long id) {
    }
}
