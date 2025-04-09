package com.example.ecommerce_app.model;


import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "Product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "productID", nullable = false, unique = true)
    private long productID;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "image_URL", nullable = false, unique = true)
    private String imageURL;

    @Column(name = "category", nullable = false)
    private String category;

    @OneToMany(mappedBy = "product")
    private List<CartItem> cartItems;

    public List<CartItem> getCartItems() {
        return cartItems;
    }
    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageURL() {
        return imageURL;
    }
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public long getProductID() {
        return productID;
    }
    public void setProductID(long productID) {
        this.productID = productID;
    }

}
