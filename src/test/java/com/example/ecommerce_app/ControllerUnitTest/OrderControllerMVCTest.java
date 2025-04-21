package com.example.ecommerce_app.ControllerUnitTest;

import com.example.ecommerce_app.DTO.AuthRequest;
import com.example.ecommerce_app.Model.LocalUser;
import com.example.ecommerce_app.Model.OrderItem;
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

import java.util.Collections;

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
        // Register and login
        LocalUser user = new LocalUser("testorder@mail.com", "orderuser", "12345678",
                "Test", "Order", "Order Address", "0123456789", "ROLE_USER");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(post("/api/users/login/username")
                        .param("username", "orderuser")
                        .param("password", "12345678"))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        token = mapper.readTree(response).get("token").asText();
    }

    @Test
    @DisplayName("Test1: Place Order successfully")
    @Transactional
    public void placeOrder_success() throws Exception {
        UserOrder order = new UserOrder();
        order.setOrderItems(Collections.singletonList(new OrderItem(1L, 2)));

        mockMvc.perform(post("/api/orders/place")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(order)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test2: Get order by ID - success")
    public void getOrderById_success() throws Exception {
        mockMvc.perform(get("/api/orders/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test3: Get order by ID - fail")
    public void getOrderById_fail() throws Exception {
        mockMvc.perform(get("/api/orders/999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test4: Cancel order - success")
    @Transactional
    public void cancelOrder_success() throws Exception {
        mockMvc.perform(delete("/api/orders/cancel/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test5: Get all orders for user")
    public void getAllOrders_success() throws Exception {
        mockMvc.perform(get("/api/orders/user")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
