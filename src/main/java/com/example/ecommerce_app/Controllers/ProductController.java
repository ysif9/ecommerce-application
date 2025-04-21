package com.example.ecommerce_app.Controllers;

import com.example.ecommerce_app.Model.Product;
import com.example.ecommerce_app.Repositories.ProductRepository;
import com.example.ecommerce_app.Services.ProductService;
import com.example.ecommerce_app.exception.ProductAlreadyExistsException;
import com.example.ecommerce_app.exception.ProductNotExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductService productService;

    public ProductController(ProductRepository productRepository, ProductService productService) {
        this.productRepository = productRepository;
        this.productService =  productService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Product getProductById(@PathVariable Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    @GetMapping("/category/{category}")
    @ResponseStatus(HttpStatus.OK)
    public List<Product> getProductsByCategory(@PathVariable String category) {
        return productRepository.findByCategory(category);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<Product> searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String category) {
        
        if (name != null && !name.isEmpty()) {
            return productRepository.findByNameContainingIgnoreCase(name);
        }
        
        if (minPrice != null && maxPrice != null) {
            return productRepository.findByPriceBetween(minPrice, maxPrice);
        }
        
        if (category != null && !category.isEmpty()) {
            return productRepository.findByCategory(category);
        }
        
        return productRepository.findAll();
    }
    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product)  {
        try{
        productService.createProduct(product);
        return ResponseEntity.ok().build();}
        catch (ProductAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @Valid @RequestBody Product product) {
        productService.updateProduct(id, product);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Product> deleteProduct(@PathVariable Long id) {
        try{
            productService.deleteProduct(id);
            return ResponseEntity.ok().build();
        }
        catch (ProductNotExistException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

    }
    }

    @GetMapping("/{min}/{max}")
    public List<Product> getProductsInRange(@PathVariable double min, @PathVariable double max) {
        return productRepository.findByPriceBetween(min, max);
    }


} 