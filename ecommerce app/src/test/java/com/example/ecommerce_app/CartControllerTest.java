package com.example.ecommerce_app;

import com.example.ecommerce_app.Controllers.CartController;
import com.example.ecommerce_app.Repositories.CartRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CartController.class)
public class CartControllerTest {
    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    CartRepository repository;


}
