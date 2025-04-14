package com.example.ecommerce_app.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "User_order")
public class UserOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OrderID", nullable = false, unique = true)
    private long orderID;

    @ManyToOne(optional = false)
    @JoinColumn(name = "LocalUser_ID", nullable = false)
    private LocalUser user;

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
}
