package com.example.ecommerce_app.ControllerUnitTest;

import com.example.ecommerce_app.Controllers.ProductController;
import com.example.ecommerce_app.Model.Product;
import com.example.ecommerce_app.Repositories.ProductRepository;
import com.example.ecommerce_app.Services.ProductService;
import com.example.ecommerce_app.exception.ProductAlreadyExistsException;
import com.example.ecommerce_app.exception.ProductNotExistException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@AutoConfigureMockMvc
class ProductControllerUnitTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        product = new Product("Mouse", 25.99, 10, "Wireless mouse", "img-url", "Electronics");
        product.setProductID(1L);
    }

    @Test
    @DisplayName("1: Test get all products")
    void getAllProducts() {
        List<Product> products = List.of(product);
        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = productController.getAllProducts();

        assertEquals(1, result.size());
        assertEquals("Mouse", result.get(0).getName());
    }

    @Test
    @DisplayName("2: Test get product by ID success")
    void getProductById_success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = productController.getProductById(1L);

        assertEquals("Mouse", result.getName());
        assertEquals(25.99, result.getPrice());
    }

    @Test
    @DisplayName("3: Test get product by ID not found")
    void getProductById_notFound() {
        when(productRepository.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            productController.getProductById(2L);
        });

        assertTrue(exception.getMessage().contains("Product not found"));
    }

    @Test
    @DisplayName("4: Test create product success")
    void createProduct_success() throws ProductAlreadyExistsException {
        ResponseEntity<Product> response = productController.createProduct(product);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).createProduct(product);
    }

    @Test
    @DisplayName("5: Test create product conflict")
    void createProduct_conflict() throws ProductAlreadyExistsException {
        doThrow(new ProductAlreadyExistsException()).when(productService).createProduct(product);

        ResponseEntity<Product> response = productController.createProduct(product);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    @DisplayName("6: Test update product success")
    void updateProduct_success() throws ProductNotExistException {
        ResponseEntity<Product> response = productController.updateProduct(1L, product);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).updateProduct(1L, product);
    }

    @Test
    @DisplayName("7: Test update product creates new")
    void updateProduct_created() throws ProductNotExistException {
        doThrow(new ProductNotExistException()).when(productService).updateProduct(1L, product);

        ResponseEntity<Product> response = productController.updateProduct(1L, product);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    @DisplayName("8: Test delete product success")
    void deleteProduct_success() throws ProductNotExistException {
        ResponseEntity<Product> response = productController.deleteProduct(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).deleteProduct(1L);
    }

    @Test
    @DisplayName("9: Test delete product not found")
    void deleteProduct_notFound() throws ProductNotExistException {
        doThrow(new ProductNotExistException()).when(productService).deleteProduct(1L);

        ResponseEntity<Product> response = productController.deleteProduct(1L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }


    @Test
    @DisplayName("10: Test get products in price range (GET /{min}/{max})")
    void getProductsInRange() {
        when(productRepository.findByPriceBetween(10.0, 50.0)).thenReturn(List.of(product));
        List<Product> result = productController.getProductsInRange(10.0, 50.0);
        assertEquals(1, result.size());
    }
}
