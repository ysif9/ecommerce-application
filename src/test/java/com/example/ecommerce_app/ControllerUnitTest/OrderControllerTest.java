package com.example.ecommerce_app.API.controller;

import com.example.ecommerce_app.model.*;
import com.example.ecommerce_app.services.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testGetOrders() throws Exception {
        LocalUser user = new LocalUser();
        UserOrder order = new UserOrder();
        order.setId(1L);
        order.setStatus("pending");

        when(orderService.getOrdersByUser(any(LocalUser.class), eq(null)))
                .thenReturn(List.of(order));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetOrderById() throws Exception {
        UserOrder order = new UserOrder();
        order.setId(1L);
        when(orderService.getOrderById(1L)).thenReturn(order);

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testPlaceOrder() throws Exception {
        UserOrder order = new UserOrder();
        order.setId(1L);
        order.setStatus("pending");

        when(orderService.placeOrder(any(LocalUser.class), anyList())).thenReturn(order);

        mockMvc.perform(post("/api/orders"))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateOrder() throws Exception {
        UserOrder order = new UserOrder();
        order.setId(1L);

        when(orderService.updateOrder(any(UserOrder.class))).thenReturn(order);

        mockMvc.perform(put("/api/orders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteOrder() throws Exception {
        doNothing().when(orderService).deleteOrder(1L);

        mockMvc.perform(delete("/api/orders/1"))
                .andExpect(status().isOk());
    }
}
