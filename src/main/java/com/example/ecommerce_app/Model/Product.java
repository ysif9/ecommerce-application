package com.example.ecommerce_app.Model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "Product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "productID", nullable = false, unique = true)
    private long productID;

    @Column(name = "name", nullable = false, unique = true)
    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @Column(name = "price", nullable = false)
    private double price;
    @NotNull
    @Column(name = "quantity", nullable = false)
    private int quantity;
    @NotNull
    @Column(name = "description", nullable = false)
    private String description;
    @NotNull
    @Column(name = "image_URL", nullable = false, unique = true)
    private String imageURL;
    @NotNull
    @Column(name = "category", nullable = false)
    private String category;

//    @OneToMany(mappedBy = "product")
//    private List<CartItem> cartItems;

    public Product(String name, double price, int quantity, String description, String imageURL, String category) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.description = description;
        this.imageURL = imageURL;
        this.category = category;
//        this.cartItems = cartItems;
    }

    public Product() {

    }

//    public List<CartItem> getCartItems() {
//        return cartItems;
//    }
//    public void setCartItems(List<CartItem> cartItems) {
//        this.cartItems = cartItems;
//    }

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
