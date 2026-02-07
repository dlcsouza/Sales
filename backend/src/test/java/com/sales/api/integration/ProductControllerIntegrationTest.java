package com.sales.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sales.api.dto.ProductRequestDTO;
import com.sales.api.entity.Product;
import com.sales.api.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    private Product product;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();

        product = Product.builder()
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("99.99"))
                .stockQuantity(100)
                .build();
        product = productRepository.save(product);
    }

    @Test
    @DisplayName("GET /api/products - Should return all products")
    void findAll_ShouldReturnAllProducts() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Test Product")));
    }

    @Test
    @DisplayName("GET /api/products/{id} - Should return product by ID")
    void findById_ShouldReturnProduct() throws Exception {
        mockMvc.perform(get("/api/products/{id}", product.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(product.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.price", is(99.99)));
    }

    @Test
    @DisplayName("GET /api/products/{id} - Should return 404 when product not found")
    void findById_ShouldReturn404_WhenProductNotFound() throws Exception {
        mockMvc.perform(get("/api/products/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/products/search - Should search products by name")
    void findByName_ShouldReturnMatchingProducts() throws Exception {
        mockMvc.perform(get("/api/products/search")
                        .param("name", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", containsString("Test")));
    }

    @Test
    @DisplayName("GET /api/products/in-stock - Should return products in stock")
    void findInStock_ShouldReturnProductsWithStock() throws Exception {
        mockMvc.perform(get("/api/products/in-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].stockQuantity", greaterThan(0)));
    }

    @Test
    @DisplayName("POST /api/products - Should create product")
    void create_ShouldCreateProduct() throws Exception {
        ProductRequestDTO request = ProductRequestDTO.builder()
                .name("New Product")
                .description("New Description")
                .price(new BigDecimal("149.99"))
                .stockQuantity(50)
                .build();

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("New Product")))
                .andExpect(jsonPath("$.price", is(149.99)));
    }

    @Test
    @DisplayName("POST /api/products - Should return 400 for invalid request")
    void create_ShouldReturn400_WhenInvalidRequest() throws Exception {
        ProductRequestDTO request = ProductRequestDTO.builder()
                .name("")
                .price(new BigDecimal("-10"))
                .stockQuantity(-5)
                .build();

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/products/{id} - Should update product")
    void update_ShouldUpdateProduct() throws Exception {
        ProductRequestDTO request = ProductRequestDTO.builder()
                .name("Updated Product")
                .description("Updated Description")
                .price(new BigDecimal("199.99"))
                .stockQuantity(200)
                .build();

        mockMvc.perform(put("/api/products/{id}", product.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Product")))
                .andExpect(jsonPath("$.price", is(199.99)));
    }

    @Test
    @DisplayName("PUT /api/products/{id} - Should return 404 when product not found")
    void update_ShouldReturn404_WhenProductNotFound() throws Exception {
        ProductRequestDTO request = ProductRequestDTO.builder()
                .name("Test")
                .price(new BigDecimal("10"))
                .stockQuantity(10)
                .build();

        mockMvc.perform(put("/api/products/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/products/{id} - Should delete product")
    void delete_ShouldDeleteProduct() throws Exception {
        mockMvc.perform(delete("/api/products/{id}", product.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/products/{id}", product.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/products/{id} - Should return 404 when product not found")
    void delete_ShouldReturn404_WhenProductNotFound() throws Exception {
        mockMvc.perform(delete("/api/products/{id}", 999L))
                .andExpect(status().isNotFound());
    }
}
