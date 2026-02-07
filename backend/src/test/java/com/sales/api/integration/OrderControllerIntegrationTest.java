package com.sales.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sales.api.dto.OrderItemRequestDTO;
import com.sales.api.dto.OrderRequestDTO;
import com.sales.api.dto.OrderStatusUpdateDTO;
import com.sales.api.entity.*;
import com.sales.api.repository.CustomerRepository;
import com.sales.api.repository.OrderRepository;
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
import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    private Customer customer;
    private Product product;
    private Order order;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
        customerRepository.deleteAll();

        customer = Customer.builder()
                .name("John Doe")
                .email("john@example.com")
                .phone("123456789")
                .address("123 Main St")
                .build();
        customer = customerRepository.save(customer);

        product = Product.builder()
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("99.99"))
                .stockQuantity(100)
                .build();
        product = productRepository.save(product);

        order = Order.builder()
                .customer(customer)
                .status(OrderStatus.PENDING)
                .totalAmount(new BigDecimal("199.98"))
                .build();

        OrderItem item = OrderItem.builder()
                .product(product)
                .quantity(2)
                .unitPrice(new BigDecimal("99.99"))
                .build();
        order.addItem(item);
        order = orderRepository.save(order);
    }

    @Test
    @DisplayName("GET /api/orders - Should return all orders")
    void findAll_ShouldReturnAllOrders() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].customerName", is("John Doe")));
    }

    @Test
    @DisplayName("GET /api/orders/{id} - Should return order by ID")
    void findById_ShouldReturnOrder() throws Exception {
        mockMvc.perform(get("/api/orders/{id}", order.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(order.getId().intValue())))
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.items", hasSize(1)));
    }

    @Test
    @DisplayName("GET /api/orders/{id} - Should return 404 when order not found")
    void findById_ShouldReturn404_WhenOrderNotFound() throws Exception {
        mockMvc.perform(get("/api/orders/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/orders/customer/{customerId} - Should return orders by customer")
    void findByCustomerId_ShouldReturnCustomerOrders() throws Exception {
        mockMvc.perform(get("/api/orders/customer/{customerId}", customer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].customerId", is(customer.getId().intValue())));
    }

    @Test
    @DisplayName("GET /api/orders/status/{status} - Should return orders by status")
    void findByStatus_ShouldReturnOrdersWithStatus() throws Exception {
        mockMvc.perform(get("/api/orders/status/{status}", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("PENDING")));
    }

    @Test
    @DisplayName("POST /api/orders - Should create order")
    void create_ShouldCreateOrder() throws Exception {
        OrderItemRequestDTO itemRequest = OrderItemRequestDTO.builder()
                .productId(product.getId())
                .quantity(1)
                .build();

        OrderRequestDTO request = OrderRequestDTO.builder()
                .customerId(customer.getId())
                .items(Arrays.asList(itemRequest))
                .build();

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.items", hasSize(1)));
    }

    @Test
    @DisplayName("POST /api/orders - Should return 404 when customer not found")
    void create_ShouldReturn404_WhenCustomerNotFound() throws Exception {
        OrderItemRequestDTO itemRequest = OrderItemRequestDTO.builder()
                .productId(product.getId())
                .quantity(1)
                .build();

        OrderRequestDTO request = OrderRequestDTO.builder()
                .customerId(999L)
                .items(Arrays.asList(itemRequest))
                .build();

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/orders - Should return 400 when insufficient stock")
    void create_ShouldReturn400_WhenInsufficientStock() throws Exception {
        OrderItemRequestDTO itemRequest = OrderItemRequestDTO.builder()
                .productId(product.getId())
                .quantity(999)
                .build();

        OrderRequestDTO request = OrderRequestDTO.builder()
                .customerId(customer.getId())
                .items(Arrays.asList(itemRequest))
                .build();

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/orders/{id}/status - Should update order status")
    void updateStatus_ShouldUpdateStatus() throws Exception {
        OrderStatusUpdateDTO request = OrderStatusUpdateDTO.builder()
                .status(OrderStatus.CONFIRMED)
                .build();

        mockMvc.perform(put("/api/orders/{id}/status", order.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CONFIRMED")));
    }

    @Test
    @DisplayName("DELETE /api/orders/{id} - Should delete pending order")
    void delete_ShouldDeletePendingOrder() throws Exception {
        mockMvc.perform(delete("/api/orders/{id}", order.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/orders/{id}", order.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/orders/{id} - Should return 404 when order not found")
    void delete_ShouldReturn404_WhenOrderNotFound() throws Exception {
        mockMvc.perform(delete("/api/orders/{id}", 999L))
                .andExpect(status().isNotFound());
    }
}
