package com.example.ecommerce_app.Services;

import com.example.ecommerce_app.Model.Product;
import com.example.ecommerce_app.exception.ProductAlreadyExistsException;
import com.example.ecommerce_app.exception.ProductNotExistException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest

public class ProductServiceTest {
    @Autowired
    ProductService productService;


    @Test
    @Transactional
    public void testAddProduct() {
        //UserName that already Exists
        Product product = new Product();
        product.setName("Wireless Mouse");
        product.setDescription("Wireless Mouse");
        product.setPrice(10.0);
        product.setCategory("Mouse");
        product.setImageURL("https://www.google.com");
        Assertions.assertThrows(ProductAlreadyExistsException.class, () -> {productService.createProduct(product);});
        //Valid registration

        product.setName("Wired Mouse");
        Assertions.assertDoesNotThrow( () -> {productService.createProduct(product);});

    }


    @Test
    @Transactional
    @DisplayName("Test 1-3: delete product once invalid, twice valid")
    public void testDeleteProduct() {
        Assertions.assertThrows(ProductNotExistException.class, () -> {productService.deleteProduct(50L);});
        Assertions.assertDoesNotThrow( () -> {productService.deleteProduct(2L);});
        Assertions.assertDoesNotThrow( () -> {productService.deleteProduct(1L);});
    }
    @Test
    @Transactional
    @DisplayName("Test 4-5: update a product that exists and one that doesn't exist")
    public void testUpdateProduct() {
        Product product = new Product();

        product.setName("Wireless Mouse");
        product.setDescription("Wireless Mouse");
        product.setPrice(10.0);
        product.setCategory("Mouse");
        product.setImageURL("https://www.google.com");
        Assertions.assertDoesNotThrow(()-> productService.updateProduct(1L,product));
        product.setName("Wired Mouse");
        Assertions.assertThrows(ProductNotExistException.class,()-> productService.updateProduct(50L, product));
    }

    @Test
    @DisplayName("Test 6: find products in a range.")
    public void testFindProductInRange() {
        List<Product> productsInRange = productService.getProductsInRange(50, 200);
        Assertions.assertEquals(6, productsInRange.size());

        for (Product product : productsInRange) {
            Assertions.assertTrue(product.getPrice() >= 50.0);
            Assertions.assertTrue(product.getPrice() <= 200.0);
        }
    }

}
