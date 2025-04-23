package com.example.ecommerce_app.ControllerUnitTest;

import com.example.ecommerce_app.DTO.AuthRequest;
import com.example.ecommerce_app.Model.LocalUser;
import com.example.ecommerce_app.Model.UserOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerMVCTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private String token;

    @BeforeEach
    void setup() throws Exception {
        // Unique email per test run
        String uniqueEmail = "testuser" + System.nanoTime() + "@mail.com";

        // Register user
        LocalUser user = new LocalUser();
        user.setEmail("testuser@mail.com");
        user.setUsername("testuser");
        user.setPassword("12345678");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setAddress("Address");
        user.setPhoneNumber("0123456789");
        user.setRole("ROLE_USER");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        // Login user to get token
        AuthRequest authRequest = new AuthRequest("testuser", "12345678");

        MvcResult result = mockMvc.perform(post("/api/users/login/username")
                        .param("username", "testuser")
                        .param("password", "12345678"))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        token = mapper.readTree(responseJson).get("token").asText();
    }

    @Test
    @DisplayName("Test1: Get all orders for authenticated user")
    void getOrders_success() throws Exception {
        mockMvc.perform(get("/api/orders")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Test2: Get order by ID success")
    void getOrderById_success() throws Exception {
        mockMvc.perform(get("/api/orders/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test3: Place an order")
    @Transactional
    void placeOrder_success() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))  // assuming an empty cart for simplicity
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test4: Update order success")
    @Transactional
    void updateOrder_success() throws Exception {
        // Step 1: Place an order (so order ID 1 exists)
        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isOk());

        // Step 2: Update the order
        UserOrder order = new UserOrder();
        order.setStatus("shipped");

        mockMvc.perform(put("/api/orders/1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(order)))
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("Test5: Delete order success")
    @Transactional
    void deleteOrder_success() throws Exception {
        // Create an order
        UserOrder Order = new UserOrder();


        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isOk());

        // Delete the order
        mockMvc.perform(delete("/api/orders/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

}



