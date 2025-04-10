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
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("")
    public CartResponse getUserCart() {
        LocalUser user = localUserService.getUserById(1L);
        Cart cart = cartService.getCartByUser(user);
        return cartService.mapToDTO(cart);
    }

    // Add item to cart
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/items")
    public CartResponse addItemToCart(@RequestParam Long productId, @RequestParam int quantity) {
        LocalUser user = localUserService.getUserById(1L); // later get from auth

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
    @PostMapping("/items/{id}")
    public CartItemResponse updateItemQuantity(@PathVariable Long id, @RequestParam int quantity ) {
        CartItem item = cartService.updateItem(id, quantity);

        return cartService.mapToResponse(item);
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
