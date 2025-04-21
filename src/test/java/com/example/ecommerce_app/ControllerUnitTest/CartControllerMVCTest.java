package com.example.ecommerce_app.ControllerUnitTest;

import com.example.ecommerce_app.DTO.AuthRequest;
import com.example.ecommerce_app.Model.LocalUser;
import com.example.ecommerce_app.Model.Product;
import com.example.ecommerce_app.Repositories.CartItemRepository;
import com.example.ecommerce_app.Repositories.CartRepository;
import com.example.ecommerce_app.Repositories.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CartControllerMVCTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper mapper;

    private String token;
    private Long testProductId;

    @BeforeEach
    void setup() throws Exception {
        // Register user (or ensure it exists already)
        LocalUser user = new LocalUser();
        user.setEmail("testuser@mail.com");
        user.setUsername("testuser");
        user.setPassword("12345678");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setAddress("Address");
        user.setPhoneNumber("0123456789");
        user.setRole("ROLE_USER");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andDo(print());

        // Login to get JWT
        AuthRequest authRequest = new AuthRequest("testuser", "12345678");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/users/login/username")
                        .param("username", "testuser")
                        .param("password", "12345678"))
                .andExpect(status().isOk()).andReturn();

        // Extract token from response JSON
        String responseJson = result.getResponse().getContentAsString();
        token = mapper.readTree(responseJson).get("token").asText();

        // Find or create a test product to use in cart tests
        Product testProduct = productRepository.findByNameIgnoreCase("Test Cart Product").orElse(null);
        if (testProduct == null) {
            testProduct = new Product("Test Cart Product", 15.0, 20, "Test product for cart", "https://image.url", "Test");
            testProduct = productRepository.save(testProduct);
        }
        testProductId = testProduct.getProductID();

        // Clear any existing cart for the test user
        try {
            clearCartForTest();
        } catch (Exception e) {
            // Ignore errors when clearing cart (it might not exist yet)
            System.out.println("Note: Cart could not be cleared during setup: " + e.getMessage());
        }
    }

    @AfterEach
    void cleanup() {
        // Clear the cart after each test
        try {
            clearCartForTest();
        } catch (Exception e) {
            // Ignore errors when clearing cart
            System.out.println("Note: Cart could not be cleared during cleanup: " + e.getMessage());
        }
    }

    private void clearCartForTest() throws Exception {
        // Just attempt to clear the cart, don't expect any specific status code
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/cart")
                        .header("Authorization", "Bearer " + token))
                .andDo(print());
    }

    @Test
    @DisplayName("Test1: Get empty cart success")
    @Transactional
    void getEmptyCart_success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/cart")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(0)));
    }

    @Test
    @DisplayName("Test2: Add item to cart success")
    @Transactional
    void addItemToCart_success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/cart/items")
                        .header("Authorization", "Bearer " + token)
                        .param("productId", testProductId.toString())
                        .param("quantity", "2"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].productId", is(testProductId.intValue())))
                .andExpect(jsonPath("$.items[0].quantity", is(2)));
    }

    @Test
    @DisplayName("Test3: Add item to cart with invalid product ID")
    @Transactional
    void addItemToCart_invalidProductId() throws Exception {
        Assertions.assertThrows(ServletException.class, () -> {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/cart/items")
                    .header("Authorization", "Bearer " + token)
                    .param("productId", "999999")
                    .param("quantity", "2"));
        });
    }

    @Test
    @DisplayName("Test4: Add existing item to cart increases quantity")
    @Transactional
    void addExistingItemToCart_increasesQuantity() throws Exception {
        // First add
        mockMvc.perform(MockMvcRequestBuilders.post("/api/cart/items")
                        .header("Authorization", "Bearer " + token)
                        .param("productId", testProductId.toString())
                        .param("quantity", "2"))
                .andExpect(status().isCreated());

        // Second add should increase quantity
        mockMvc.perform(MockMvcRequestBuilders.post("/api/cart/items")
                        .header("Authorization", "Bearer " + token)
                        .param("productId", testProductId.toString())
                        .param("quantity", "3"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].productId", is(testProductId.intValue())))
                .andExpect(jsonPath("$.items[0].quantity", is(5))); // 2 + 3 = 5
    }

    @Test
    @DisplayName("Test5: Get cart with items success")
    @Transactional
    void getCartWithItems_success() throws Exception {
        // Add item to cart
        mockMvc.perform(MockMvcRequestBuilders.post("/api/cart/items")
                        .header("Authorization", "Bearer " + token)
                        .param("productId", testProductId.toString())
                        .param("quantity", "2"))
                .andExpect(status().isCreated());

        // Get cart and verify item is there
        mockMvc.perform(MockMvcRequestBuilders.get("/api/cart")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].productId", is(testProductId.intValue())))
                .andExpect(jsonPath("$.items[0].quantity", is(2)));
    }

    @Test
    @DisplayName("Test6: Get item details success")
    @Transactional
    void getItemDetails_success() throws Exception {
        // Add item to cart
        MvcResult addResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/cart/items")
                        .header("Authorization", "Bearer " + token)
                        .param("productId", testProductId.toString())
                        .param("quantity", "2"))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract item ID from response
        String responseJson = addResult.getResponse().getContentAsString();
        long itemId = mapper.readTree(responseJson).get("items").get(0).get("id").asLong();

        // Get item details
        mockMvc.perform(MockMvcRequestBuilders.get("/api/cart/items/" + itemId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) itemId)))
                .andExpect(jsonPath("$.productId", is(testProductId.intValue())))
                .andExpect(jsonPath("$.quantity", is(2)));
    }

    @Test
    @DisplayName("Test7: Get item details with invalid ID")
    @Transactional
    void getItemDetails_invalidId() throws Exception {
        Assertions.assertThrows(ServletException.class, () -> {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/cart/items/999999")
                    .header("Authorization", "Bearer " + token));
        });
    }

    @Test
    @DisplayName("Test8: Update item quantity success")
    @Transactional
    void updateItemQuantity_success() throws Exception {
        // Add item to cart
        MvcResult addResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/cart/items")
                        .header("Authorization", "Bearer " + token)
                        .param("productId", testProductId.toString())
                        .param("quantity", "2"))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract item ID from response
        String responseJson = addResult.getResponse().getContentAsString();
        long itemId = mapper.readTree(responseJson).get("items").get(0).get("id").asLong();

        // Update item quantity
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/cart/items/" + itemId)
                        .header("Authorization", "Bearer " + token)
                        .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) itemId)))
                .andExpect(jsonPath("$.quantity", is(5)));

        // Verify cart was updated
        mockMvc.perform(MockMvcRequestBuilders.get("/api/cart")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].quantity", is(5)));
    }

    @Test
    @DisplayName("Test9: Update item quantity with invalid ID")
    @Transactional
    void updateItemQuantity_invalidId() throws Exception {
        Assertions.assertThrows(ServletException.class, () -> {
            mockMvc.perform(MockMvcRequestBuilders.patch("/api/cart/items/999999")
                    .header("Authorization", "Bearer " + token)
                    .param("quantity", "5"));
        });
    }

    @Test
    @DisplayName("Test10: Remove item success")
    @Transactional
    void removeItem_success() throws Exception {
        // Add item to cart
        MvcResult addResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/cart/items")
                        .header("Authorization", "Bearer " + token)
                        .param("productId", testProductId.toString())
                        .param("quantity", "2"))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract item ID from response
        String responseJson = addResult.getResponse().getContentAsString();
        long itemId = mapper.readTree(responseJson).get("items").get(0).get("id").asLong();

        // Remove item
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/cart/items/" + itemId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        // Verify cart is empty
        mockMvc.perform(MockMvcRequestBuilders.get("/api/cart")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(0)));
    }

    @Test
    @DisplayName("Test11: Remove item with invalid ID")
    @Transactional
    void removeItem_invalidId() throws Exception {
        Assertions.assertThrows(ServletException.class, () -> {
            mockMvc.perform(MockMvcRequestBuilders.delete("/api/cart/items/999999")
                    .header("Authorization", "Bearer " + token));
        });
    }

    @Test
    @DisplayName("Test12: Clear cart success")
    @Transactional
    void clearCart_success() throws Exception {
        // Add item to cart
        mockMvc.perform(MockMvcRequestBuilders.post("/api/cart/items")
                        .header("Authorization", "Bearer " + token)
                        .param("productId", testProductId.toString())
                        .param("quantity", "2"))
                .andExpect(status().isCreated());

        // Clear cart
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/cart")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        // Verify cart is empty
        mockMvc.perform(MockMvcRequestBuilders.get("/api/cart")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(0)));
    }

    @Test
    @DisplayName("Test13: Unauthorized access to cart")
    @Transactional
    void unauthorizedAccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/cart"))
                .andExpect(status().isUnauthorized());
    }
}
