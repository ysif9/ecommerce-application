package com.example.ecommerce_app.Controllers;

import com.example.ecommerce_app.DTO.CartResponse;
import com.example.ecommerce_app.DTO.CartItemResponse;
import com.example.ecommerce_app.Services.CartService;
import com.example.ecommerce_app.Services.LocalUserService;
import com.example.ecommerce_app.Model.Cart;
import com.example.ecommerce_app.Model.CartItem;
import com.example.ecommerce_app.Model.LocalUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final LocalUserService localUserService;

    public CartController(CartService cartService, LocalUserService localUserService) {
        this.cartService = cartService;
        this.localUserService = localUserService;
    }

    // Get Cart Details for user (currently user is hardcoded, should update when auth is added)
    @GetMapping("")
    public ResponseEntity<CartResponse> getUserCart() {
        LocalUser user = localUserService.getUserById(1L);
        Cart cart = cartService.getCartByUser(user);
        return ResponseEntity.ok(cartService.mapToDTO(cart));
    }

    // Add item to cart
    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItemToCart(@RequestParam Long productId, @RequestParam int quantity) {
        LocalUser user = localUserService.getUserById(1L); // later get from auth

        Cart cart = cartService.addItemToCart(user, productId, quantity);
        CartResponse cartResponse = cartService.mapToDTO(cart);

        return ResponseEntity.ok(cartResponse);
    }

    // Get item details
    @GetMapping("/items/{id}")
    public ResponseEntity<CartItemResponse> getItemDetails(@PathVariable Long id) {
        CartItem itemDetails = cartService.getItemDetails(id);

        CartItemResponse cartItemResponse = cartService.mapToResponse(itemDetails);

        return ResponseEntity.ok(cartItemResponse);
    }

    // Update item quantity
    @PostMapping("/items/{id}")
    public ResponseEntity<CartItemResponse> updateItemQuantity(@PathVariable Long id, @RequestParam int quantity ) {
        CartItem item = cartService.updateItem(id, quantity);

        CartItemResponse cartItemResponse = cartService.mapToResponse(item);

        return ResponseEntity.ok(cartItemResponse);
    }

    // Remove item from cart
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/items/{id}")
    public void removeItem(@PathVariable Long id) {
        cartService.removeItem(id);
    }

    // Clear cart
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("")
    public void clearCart() {
        LocalUser user = localUserService.getUserById(1L); // later get from auth
        cartService.clearCart(user);
    }


}
