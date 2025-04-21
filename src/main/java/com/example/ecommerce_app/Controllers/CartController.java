package com.example.ecommerce_app.Controllers;

import com.example.ecommerce_app.DTO.CartItemResponse;
import com.example.ecommerce_app.DTO.CartResponse;
import com.example.ecommerce_app.Model.Cart;
import com.example.ecommerce_app.Model.CartItem;
import com.example.ecommerce_app.Model.LocalUser;
import com.example.ecommerce_app.Services.AuthService;
import com.example.ecommerce_app.Services.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final AuthService authService;

    public CartController(CartService cartService, AuthService authService) {
        this.cartService = cartService;
        this.authService = authService;
    }

    // Get Cart Details for user (currently user is hardcoded, should update when auth is added)
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("")
    public CartResponse getUserCart(Authentication authentication) {
        LocalUser user = authService.getUserFromAuthentication(authentication);
        Cart cart = cartService.getCartByUser(user);
        return cartService.mapToDTO(cart);
    }

    // Add item to the cart
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/items")
    public CartResponse addItemToCart(Authentication authentication, @RequestParam Long productId, @RequestParam int quantity) {
        LocalUser user = authService.getUserFromAuthentication(authentication);

        Cart cart = cartService.addItemToCart(user, productId, quantity);

        return cartService.mapToDTO(cart);
    }

    // Get item details
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/items/{id}")
    public CartItemResponse getItemDetails(@PathVariable Long id) {
        CartItem itemDetails = cartService.getItemDetails(id);

        return cartService.mapToResponse(itemDetails);
    }

    // Update item quantity
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/items/{id}")
    public CartItemResponse updateItemQuantity(@PathVariable Long id, @RequestParam int quantity) {
        CartItem item = cartService.updateItem(id, quantity);

        return cartService.mapToResponse(item);
    }

    // Remove item from the cart
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/items/{id}")
    public void removeItem(@PathVariable Long id) {
        cartService.removeItem(id);
    }

    // Clear cart
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("")
    public void clearCart(Authentication authentication) {
        LocalUser user = authService.getUserFromAuthentication(authentication);
        cartService.clearCart(user);
    }

}
