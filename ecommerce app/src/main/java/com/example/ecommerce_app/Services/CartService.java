package com.example.ecommerce_app.Services;

import com.example.ecommerce_app.DTO.CartResponse;
import com.example.ecommerce_app.DTO.CartItemResponse;
import com.example.ecommerce_app.Repositories.CartItemRepository;
import com.example.ecommerce_app.Repositories.CartRepository;
import com.example.ecommerce_app.Repositories.ProductRepository;
import com.example.ecommerce_app.Model.Cart;
import com.example.ecommerce_app.Model.CartItem;
import com.example.ecommerce_app.Model.LocalUser;
import com.example.ecommerce_app.Model.Product;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    public CartItemResponse mapToResponse(CartItem cartItem) {
        CartItemResponse response = new CartItemResponse();
        response.setProductId(cartItem.getProduct().getProductID());
        response.setProductName(cartItem.getProduct().getName());
        response.setPrice(cartItem.getProduct().getPrice());
        response.setQuantity(cartItem.getQuantity());
        return response;
    }

    public CartResponse mapToDTO(Cart cart) {
        CartResponse cartResponse = new CartResponse();
        cartResponse.setUserId(cart.getUser().getID());
        cartResponse.setId(cart.getID());
        cartResponse.setItems(cart.getItems().stream()
                .map(this::mapToResponse)
                .toList());
        return cartResponse;
    }

    public List<Cart> getAllCarts() {
        return cartRepository.findAll();
    }

    public Cart getCartByUser(LocalUser user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    return cartRepository.save(cart);
                });
    }

    public Cart addItemToCart(LocalUser user, Long productId, int quantity) {
        Cart cart = getCartByUser(user);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id " + productId));

        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getProduct().getProductID() == productId)
                .findFirst();

        if(existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setCart(cart);
            cart.getItems().add(newItem);
        }
        return cartRepository.save(cart);
    }

    public CartItem getItemDetails(Long id) {
        return cartItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found with id " + id));
    }

    public CartItem updateItem(Long id, int quantity) {
        CartItem item = cartItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found with id " + id));
        item.setQuantity(quantity);
        return cartItemRepository.save(item);
    }


    public void removeItem(Long id) {
        CartItem item = cartItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found with id " + id));
        Cart cart = item.getCart();
        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        cartRepository.save(cart);
    }

    public void clearCart(LocalUser user) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found for user " + user.getID()));
        cart.getItems().clear();
        cartRepository.save(cart);
    }
}
