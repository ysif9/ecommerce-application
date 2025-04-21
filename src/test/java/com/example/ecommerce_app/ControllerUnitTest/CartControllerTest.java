package com.example.ecommerce_app.ControllerUnitTest;

import com.example.ecommerce_app.Controllers.CartController;
import com.example.ecommerce_app.DTO.CartItemResponse;
import com.example.ecommerce_app.DTO.CartResponse;
import com.example.ecommerce_app.Model.Cart;
import com.example.ecommerce_app.Model.CartItem;
import com.example.ecommerce_app.Model.LocalUser;
import com.example.ecommerce_app.Model.Product;
import com.example.ecommerce_app.Services.AuthService;
import com.example.ecommerce_app.Services.CartService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartService cartService;

    @Mock
    private AuthService authService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CartController cartController;

    private LocalUser user;
    private Cart cart;
    private CartItem cartItem;
    private CartResponse cartResponse;
    private CartItemResponse cartItemResponse;

    @BeforeEach
    void setUp() {
        // Initialize test user
        user = new LocalUser();
        user.setID(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        // Initialize test product
        Product product = new Product();
        product.setProductID(1L);
        product.setName("Test Product");
        product.setPrice(10.0f);
        product.setQuantity(100);

        // Initialize test cart item
        cartItem = new CartItem();
        cartItem.setCartItem_id(1L);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);

        // Initialize test cart
        cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        List<CartItem> items = new ArrayList<>();
        items.add(cartItem);
        cart.setItems(items);

        // Initialize cart item response
        cartItemResponse = new CartItemResponse();
        cartItemResponse.setId(1L);
        cartItemResponse.setProductId(1L);
        cartItemResponse.setProductName("Test Product");
        cartItemResponse.setPrice(10.0f);
        cartItemResponse.setQuantity(2);

        // Initialize cart response
        cartResponse = new CartResponse();
        cartResponse.setId(1L);
        cartResponse.setUserId(1L);
        List<CartItemResponse> itemResponses = new ArrayList<>();
        itemResponses.add(cartItemResponse);
        cartResponse.setItems(itemResponses);

        // Mock authentication
        when(authService.getUserFromAuthentication(authentication)).thenReturn(user);
    }

    @AfterEach
    void tearDown() {
        reset(cartService, authService);
    }

    @Test
    @DisplayName("Test1: Get user cart success")
    void getUserCart_success() {
        // Arrange
        when(cartService.getCartByUser(user)).thenReturn(cart);
        when(cartService.mapToDTO(cart)).thenReturn(cartResponse);

        // Act
        CartResponse response = cartController.getUserCart(authentication);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getUserId());
        assertEquals(1, response.getItems().size());
        assertEquals(1L, response.getItems().get(0).getId());
        assertEquals("Test Product", response.getItems().get(0).getProductName());

        verify(authService, times(1)).getUserFromAuthentication(authentication);
        verify(cartService, times(1)).getCartByUser(user);
        verify(cartService, times(1)).mapToDTO(cart);
    }

    @Test
    @DisplayName("Test2: Add item to cart success")
    void addItemToCart_success() {
        // Arrange
        Long productId = 1L;
        int quantity = 2;
        when(cartService.addItemToCart(user, productId, quantity)).thenReturn(cart);
        when(cartService.mapToDTO(cart)).thenReturn(cartResponse);

        // Act
        CartResponse response = cartController.addItemToCart(authentication, productId, quantity);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1, response.getItems().size());

        verify(authService, times(1)).getUserFromAuthentication(authentication);
        verify(cartService, times(1)).addItemToCart(user, productId, quantity);
        verify(cartService, times(1)).mapToDTO(cart);
    }

    @Test
    @DisplayName("Test3: Get item details success")
    void getItemDetails_success() {
        // Arrange
        Long itemId = 1L;
        when(cartService.getItemDetails(itemId)).thenReturn(cartItem);
        when(cartService.mapToResponse(cartItem)).thenReturn(cartItemResponse);

        // Act
        CartItemResponse response = cartController.getItemDetails(itemId);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Product", response.getProductName());
        assertEquals(2, response.getQuantity());

        verify(cartService, times(1)).getItemDetails(itemId);
        verify(cartService, times(1)).mapToResponse(cartItem);
    }

    @Test
    @DisplayName("Test4: Update item quantity success")
    void updateItemQuantity_success() {
        // Arrange
        Long itemId = 1L;
        int newQuantity = 5;
        when(cartService.updateItem(itemId, newQuantity)).thenReturn(cartItem);
        when(cartService.mapToResponse(cartItem)).thenReturn(cartItemResponse);

        // Act
        CartItemResponse response = cartController.updateItemQuantity(itemId, newQuantity);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());

        verify(cartService, times(1)).updateItem(itemId, newQuantity);
        verify(cartService, times(1)).mapToResponse(cartItem);
    }

    @Test
    @DisplayName("Test5: Remove item success")
    void removeItem_success() {
        // Arrange
        Long itemId = 1L;
        doNothing().when(cartService).removeItem(itemId);

        // Act
        cartController.removeItem(itemId);

        // Assert
        verify(cartService, times(1)).removeItem(itemId);
    }

    @Test
    @DisplayName("Test6: Clear cart success")
    void clearCart_success() {
        // Arrange
        doNothing().when(cartService).clearCart(user);

        // Act
        cartController.clearCart(authentication);

        // Assert
        verify(authService, times(1)).getUserFromAuthentication(authentication);
        verify(cartService, times(1)).clearCart(user);
    }
}
