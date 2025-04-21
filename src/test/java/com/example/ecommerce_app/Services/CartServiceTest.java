package com.example.ecommerce_app.Services;
import com.example.ecommerce_app.DTO.CartItemResponse;
import com.example.ecommerce_app.DTO.CartResponse;
import com.example.ecommerce_app.Model.Cart;
import com.example.ecommerce_app.Model.CartItem;
import com.example.ecommerce_app.Model.LocalUser;
import com.example.ecommerce_app.Model.Product;
import com.example.ecommerce_app.Repositories.CartItemRepository;
import com.example.ecommerce_app.Repositories.CartRepository;
import com.example.ecommerce_app.Repositories.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartService cartService;

    private LocalUser user;
    private Product product;
    private Cart cart;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        user = new LocalUser();
        user.setID(1L);

        product = new Product();
        product.setProductID(1L);
        product.setName("Test Product");
        product.setPrice(100.0);

        cartItem = new CartItem();
        cartItem.setCartItem_id(1L);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);

        cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setItems(new ArrayList<>());
        cart.getItems().add(cartItem);
        cartItem.setCart(cart);
    }

    @Test
    @DisplayName("1: Test map to response function")
    void testMapToResponse() {
        CartItemResponse response = cartService.mapToResponse(cartItem);
        
        assertEquals(product.getProductID(), response.getProductId());
        assertEquals(product.getName(), response.getProductName());
        assertEquals(product.getPrice(), response.getPrice());
        assertEquals(cartItem.getQuantity(), response.getQuantity());
    }

    @Test
    @DisplayName("2: Test map to DTO function")
    void testMapToDTO() {
        CartResponse response = cartService.mapToDTO(cart);
        
        assertEquals(user.getID(), response.getUserId());
        assertEquals(cart.getId(), response.getId());
        assertEquals(1, response.getItems().size());
        
        CartItemResponse itemResponse = response.getItems().getFirst();
        assertEquals(product.getProductID(), itemResponse.getProductId());
        assertEquals(product.getName(), itemResponse.getProductName());
        assertEquals(product.getPrice(), itemResponse.getPrice());
        assertEquals(cartItem.getQuantity(), itemResponse.getQuantity());
    }

    @Test
    @DisplayName("3: Test get all carts")
    // add more than 1
    void testGetAllCarts() {
        List<Cart> expectedCarts = List.of(cart);
        when(cartRepository.findAll()).thenReturn(expectedCarts);

        List<Cart> result = cartService.getAllCarts();
        
        assertEquals(expectedCarts, result);
        verify(cartRepository).findAll();
    }

    @Test
    @DisplayName("4: Test get cart by user")
    void testGetCartByUser_ExistingCart() {
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        Cart result = cartService.getCartByUser(user);
        
        assertEquals(cart, result);
        verify(cartRepository).findByUser(user);
        verify(cartRepository, never()).save(any());
    }

    @Test
    @DisplayName("5: Test get cart by user - cart not found")
    void testGetCartByUser_NewCart() {
        when(cartRepository.findByUser(user)).thenReturn(Optional.empty());
        when(cartRepository.save(any())).thenReturn(cart);

        Cart result = cartService.getCartByUser(user);
        
        assertNotNull(result);
        assertEquals(user, result.getUser());
        verify(cartRepository).findByUser(user);
        verify(cartRepository).save(any());
    }

    @Test
    @DisplayName("6: Test add item to cart")
    void testAddItemToCart_AddingNewItem() {
        cart.setItems(new ArrayList<>()); // Ensure it's empty

        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepository.save(any())).thenReturn(cart);

        Cart result = cartService.addItemToCart(user, 1L, 3);

        assertEquals(1, cart.getItems().size()); // Now this passes
        assertEquals(product, cart.getItems().getFirst().getProduct());
        assertEquals(3, cart.getItems().getFirst().getQuantity());
        verify(cartRepository).save(cart);
    }


    @Test
    @DisplayName("7: Test add item to cart - incrementing existing item")
    void testAddItemToCart_IncrementExistingItem() {
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepository.save(any())).thenReturn(cart);

        Cart result = cartService.addItemToCart(user, 1L, 3);

        assertEquals(1, cart.getItems().size());
        assertEquals(5, cartItem.getQuantity());
        verify(cartRepository).save(cart);
    }

    @Test
    @DisplayName("8: Test add item to cart - adding to empty cart")
    void testAddItemToCart_ThrowsExceptionWhenProductNotFound() {
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                cartService.addItemToCart(user, 1L, 3));
    }

    @Test
    @DisplayName("9: Test requesting cart items details")
    void testGetItemDetails() {
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));

        CartItem result = cartService.getItemDetails(1L);
        
        assertEquals(cartItem, result);
        verify(cartItemRepository).findById(1L);
    }

    @Test
    @DisplayName("10: Test requesting cart items details - tem not found")
    void testGetItemDetails_NotFound() {
        when(cartItemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> 
            cartService.getItemDetails(1L));
    }

    @Test
    @DisplayName("11: Test removing cart item")
    void testRemoveItem() {
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));
        when(cartRepository.save(any())).thenReturn(cart);

        cartService.removeItem(1L);
        
        assertTrue(cart.getItems().isEmpty());
        verify(cartItemRepository).delete(cartItem);
        verify(cartRepository).save(cart);
    }

    @Test
    @DisplayName("12: Test clear cart")
    void testClearCart() {
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any())).thenReturn(cart);

        cartService.clearCart(user);
        
        assertTrue(cart.getItems().isEmpty());
        verify(cartRepository).save(cart);
    }

    @AfterEach
    void tearDown() {
        cart.getItems().clear();
        reset(cartRepository, cartItemRepository, productRepository);
    }

} 