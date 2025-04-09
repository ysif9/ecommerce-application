package com.example.ecommerce_app.model;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "User_cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Cardt_ID", nullable = false, unique = true)
    private long ID;

    @OneToOne(optional = false, orphanRemoval = true)
    @JoinColumn(name = "User_id", nullable = false)
    private LocalUser user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();


    public List<CartItem> getItems() {
        return items;
    }
    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public LocalUser getUser() {
        return user;
    }
    public void setUser(LocalUser user) {
        this.user = user;
    }

    public long getID() {
        return ID;
    }
    public void setID(long ID) {
        this.ID = ID;
    }
}
