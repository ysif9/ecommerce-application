package com.example.ecommerce_app.ControllerUnitTest;

import com.example.ecommerce_app.DTO.AuthRequest;
import com.example.ecommerce_app.Model.LocalUser;
import com.example.ecommerce_app.Model.Product;
import com.example.ecommerce_app.Repositories.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerMVCTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper mapper;

    private String token;

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
    }

    @Test
    @DisplayName("Test1: Create product post request success")
    @Transactional
    void createProduct_success() throws Exception {
        Product product = new Product("Test Product", 20.0, 10, "Nice product", "https://image.url", "Electronics");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(product)))
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("Test2: Create product post request Product already exists")
    @Transactional
    void createProduct_fail() throws Exception {
        Product product = new Product("Laptop", 20.0, 10, "Nice product", "https://image.url", "Electronics");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(product)))
                .andExpect(status().is(HttpStatus.CONFLICT.value()));
    }

    @Test
    @DisplayName("Test3: Get product with ID success")
    public void getProductById_success() throws Exception {
          mockMvc.perform(MockMvcRequestBuilders.get("/api/products/1").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test4: Get product with ID fail")
    public void getProductById_fail() throws Exception {
        Assertions.assertThrows(ServletException.class,() -> mockMvc.perform(MockMvcRequestBuilders.get("/api/products/50").header("Authorization", "Bearer " + token)));
    }
    @Test
    @DisplayName("Test5: Get product with Category success")
    public void getProductByCategory_success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/category/Electronics").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(5)));
    }
    @Test
    @DisplayName("Test6: Get product with Category fail")
    public void getProductByCategory_fail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/category/NotValid").header("Authorization", "Bearer " + token))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Test7: search product with name success")
    void searchByName() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/search").header("Authorization", "Bearer " + token)
                        .param("name", "Laptop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", containsStringIgnoringCase("Laptop")));
    }

    @Test
    @DisplayName("Test8: search product with category success")
    void searchByCategory() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/search").header("Authorization", "Bearer " + token)
                        .param("category", "Electronics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)));
    }

    @Test
    @DisplayName("Test9: search product without parameters success")
    void searchWithoutParams_returnsAll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/search").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)));
    }

    @Test
    @DisplayName("Test10: Test update product success")
    @Transactional
    void updateProduct_success() throws Exception {
        Product testProduct = new Product();
        testProduct.setName("Test Product");
        testProduct.setPrice(20.0f);
        testProduct.setCategory("Electronics");
        testProduct.setDescription("Test Description");
        testProduct.setImageURL("https://image.url");
        testProduct.setQuantity(50);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/products/1").header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON).
                content(mapper.writeValueAsString(testProduct))).andExpect(status().isOk());
    }
    @Test
    @DisplayName("Test11: Test update product success id doesn't  exist, new created")
    @Transactional
    void updateProduct_idNotExist() throws Exception {
        Product testProduct = new Product();
        testProduct.setName("Test Product");
        testProduct.setPrice(20.0f);
        testProduct.setCategory("Electronics");
        testProduct.setDescription("Test Description");
        testProduct.setImageURL("https://image.url");
        testProduct.setQuantity(50);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/products/50").header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON).
                content(mapper.writeValueAsString(testProduct))).andExpect(status().is(HttpStatus.CREATED.value()));
    }
    @Test
    @DisplayName("Test12: Test update product fail incomplete product")
    @Transactional
    void updateProduct_incompleteProduct() throws Exception {
        Product testProduct = new Product();


        testProduct.setCategory("Electronics");
        testProduct.setDescription("Test Description");
        testProduct.setImageURL("https://image.url");
        testProduct.setQuantity(50);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/products/1").header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON).
                content(mapper.writeValueAsString(testProduct))).andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }
    @Test
    @DisplayName("Test13: Test delete product success")
    @Transactional
    void deleteProduct_success() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/products/1").header("Authorization", "Bearer " + token)).
                andExpect(status().isOk());

    }
    @Test
    @DisplayName("Test14: Test delete product fail id doesn't  exist")
    @Transactional
    void deleteProduct_idNotExist() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.put("/api/products/50").header("Authorization", "Bearer " + token)).
                andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }
    @Test
    @DisplayName("Test15: Find products in range with valid range")
    public void findProductsInRange_validRange() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/30/200").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(7)));
    }
    @Test
    @DisplayName("Test16: Find products in range no results")
    public void findProductsInRange_noResults() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/1000/2000").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    void getAllProducts_shouldReturnList() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products").header("Authorization", "Bearer " + token))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
