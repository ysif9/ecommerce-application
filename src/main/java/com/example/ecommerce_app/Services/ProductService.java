package com.example.ecommerce_app.Services;

import com.example.ecommerce_app.Model.Product;
import com.example.ecommerce_app.Repositories.ProductRepository;
import com.example.ecommerce_app.exception.ProductAlreadyExistsException;
import com.example.ecommerce_app.exception.ProductNotExistException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
private ProductRepository productRepository;

public ProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
}

public Product createProduct(Product product) throws ProductAlreadyExistsException {
if(productRepository.findByNameIgnoreCase(product.getName()).isPresent()){
    throw new ProductAlreadyExistsException();
}
return productRepository.save(product);
}

public Product updateProduct(Long id,Product product)  {
 Product newData = new Product();
 Optional<Product> productOptional = productRepository.findById(id);
 newData.setName(product.getName());
 newData.setPrice(product.getPrice());
 newData.setQuantity(product.getQuantity());
 newData.setCategory(product.getCategory());
 return productRepository.save(newData);
}

public void deleteProduct(Long id) throws ProductNotExistException {
    Optional<Product> productOptional = productRepository.findById(id);
    if(productOptional.isPresent()){
    productRepository.deleteById(id);}
    else{
        throw new ProductNotExistException();
    }
}

public List<Product> getProductsInRange(double low, double high) {
    List<Product> products = productRepository.findByPriceBetween(low, high);
    return products;
}

}
