package com.sales.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sales.api.dto.CustomerRequestDTO;
import com.sales.api.entity.Customer;
import com.sales.api.repository.CustomerRepository;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();

        customer = Customer.builder()
                .name("John Doe")
                .email("john@example.com")
                .phone("123456789")
                .address("123 Main St")
                .build();
        customer = customerRepository.save(customer);
    }

    @Test
    @DisplayName("GET /api/customers - Should return all customers")
    void findAll_ShouldReturnAllCustomers() throws Exception {
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("John Doe")))
                .andExpect(jsonPath("$[0].email", is("john@example.com")));
    }

    @Test
    @DisplayName("GET /api/customers/{id} - Should return customer by ID")
    void findById_ShouldReturnCustomer() throws Exception {
        mockMvc.perform(get("/api/customers/{id}", customer.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(customer.getId().intValue())))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")));
    }

    @Test
    @DisplayName("GET /api/customers/{id} - Should return 404 when customer not found")
    void findById_ShouldReturn404_WhenCustomerNotFound() throws Exception {
        mockMvc.perform(get("/api/customers/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/customers - Should create customer")
    void create_ShouldCreateCustomer() throws Exception {
        CustomerRequestDTO request = CustomerRequestDTO.builder()
                .name("Jane Doe")
                .email("jane@example.com")
                .phone("987654321")
                .address("456 New St")
                .build();

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Jane Doe")))
                .andExpect(jsonPath("$.email", is("jane@example.com")));
    }

    @Test
    @DisplayName("POST /api/customers - Should return 400 when email already exists")
    void create_ShouldReturn400_WhenEmailExists() throws Exception {
        CustomerRequestDTO request = CustomerRequestDTO.builder()
                .name("Another John")
                .email("john@example.com")
                .phone("111111111")
                .address("789 Other St")
                .build();

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/customers - Should return 400 for invalid request")
    void create_ShouldReturn400_WhenInvalidRequest() throws Exception {
        CustomerRequestDTO request = CustomerRequestDTO.builder()
                .name("")
                .email("invalid-email")
                .build();

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/customers/{id} - Should update customer")
    void update_ShouldUpdateCustomer() throws Exception {
        CustomerRequestDTO request = CustomerRequestDTO.builder()
                .name("John Updated")
                .email("john.updated@example.com")
                .phone("999999999")
                .address("999 Updated St")
                .build();

        mockMvc.perform(put("/api/customers/{id}", customer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John Updated")))
                .andExpect(jsonPath("$.email", is("john.updated@example.com")));
    }

    @Test
    @DisplayName("PUT /api/customers/{id} - Should return 404 when customer not found")
    void update_ShouldReturn404_WhenCustomerNotFound() throws Exception {
        CustomerRequestDTO request = CustomerRequestDTO.builder()
                .name("Test")
                .email("test@example.com")
                .build();

        mockMvc.perform(put("/api/customers/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/customers/{id} - Should delete customer")
    void delete_ShouldDeleteCustomer() throws Exception {
        mockMvc.perform(delete("/api/customers/{id}", customer.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/customers/{id}", customer.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/customers/{id} - Should return 404 when customer not found")
    void delete_ShouldReturn404_WhenCustomerNotFound() throws Exception {
        mockMvc.perform(delete("/api/customers/{id}", 999L))
                .andExpect(status().isNotFound());
    }
}
