package com.sales.api.service;

import com.sales.api.dto.OrderDTO;
import com.sales.api.dto.OrderItemRequestDTO;
import com.sales.api.dto.OrderRequestDTO;
import com.sales.api.entity.*;
import com.sales.api.exception.BusinessException;
import com.sales.api.exception.ResourceNotFoundException;
import com.sales.api.repository.CustomerRepository;
import com.sales.api.repository.OrderRepository;
import com.sales.api.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private OrderService orderService;

    private Customer customer;
    private Product product;
    private Order order;
    private OrderRequestDTO orderRequest;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .build();

        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(new BigDecimal("99.99"))
                .stockQuantity(100)
                .build();

        OrderItem orderItem = OrderItem.builder()
                .id(1L)
                .product(product)
                .quantity(2)
                .unitPrice(new BigDecimal("99.99"))
                .build();

        order = Order.builder()
                .id(1L)
                .customer(customer)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .totalAmount(new BigDecimal("199.98"))
                .items(new ArrayList<>(Arrays.asList(orderItem)))
                .build();
        orderItem.setOrder(order);

        OrderItemRequestDTO itemRequest = OrderItemRequestDTO.builder()
                .productId(1L)
                .quantity(2)
                .build();

        orderRequest = OrderRequestDTO.builder()
                .customerId(1L)
                .items(Arrays.asList(itemRequest))
                .build();
    }

    @Test
    @DisplayName("Should return all orders")
    void findAll_ShouldReturnAllOrders() {
        when(orderRepository.findAllWithItems()).thenReturn(Arrays.asList(order));

        List<OrderDTO> result = orderService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCustomerName()).isEqualTo("John Doe");
        verify(orderRepository, times(1)).findAllWithItems();
    }

    @Test
    @DisplayName("Should return order by ID")
    void findById_ShouldReturnOrder_WhenOrderExists() {
        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.of(order));

        OrderDTO result = orderService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING);
        verify(orderRepository, times(1)).findByIdWithItems(1L);
    }

    @Test
    @DisplayName("Should throw exception when order not found")
    void findById_ShouldThrowException_WhenOrderNotFound() {
        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.findById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order not found");

        verify(orderRepository, times(1)).findByIdWithItems(1L);
    }

    @Test
    @DisplayName("Should return orders by customer ID")
    void findByCustomerId_ShouldReturnCustomerOrders() {
        when(orderRepository.findByCustomerId(1L)).thenReturn(Arrays.asList(order));

        List<OrderDTO> result = orderService.findByCustomerId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCustomerId()).isEqualTo(1L);
        verify(orderRepository, times(1)).findByCustomerId(1L);
    }

    @Test
    @DisplayName("Should return orders by status")
    void findByStatus_ShouldReturnOrdersWithStatus() {
        when(orderRepository.findByStatus(OrderStatus.PENDING)).thenReturn(Arrays.asList(order));

        List<OrderDTO> result = orderService.findByStatus(OrderStatus.PENDING);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(OrderStatus.PENDING);
        verify(orderRepository, times(1)).findByStatus(OrderStatus.PENDING);
    }

    @Test
    @DisplayName("Should create order successfully")
    void create_ShouldCreateOrder_WhenValidRequest() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        doNothing().when(productService).updateStock(anyLong(), anyInt());
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderDTO result = orderService.create(orderRequest);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING);
        verify(customerRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw exception when customer not found during order creation")
    void create_ShouldThrowException_WhenCustomerNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.create(orderRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer not found");

        verify(customerRepository, times(1)).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw exception when product not found during order creation")
    void create_ShouldThrowException_WhenProductNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.create(orderRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found");

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw exception when insufficient stock")
    void create_ShouldThrowException_WhenInsufficientStock() {
        product.setStockQuantity(1);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> orderService.create(orderRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Insufficient stock");

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should update order status successfully")
    void updateStatus_ShouldUpdateStatus_WhenValidTransition() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderDTO result = orderService.updateStatus(1L, OrderStatus.CONFIRMED);

        assertThat(result).isNotNull();
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw exception when updating cancelled order")
    void updateStatus_ShouldThrowException_WhenOrderCancelled() {
        order.setStatus(OrderStatus.CANCELLED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.updateStatus(1L, OrderStatus.CONFIRMED))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Cannot update status of a cancelled order");

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should restore stock when cancelling order")
    void updateStatus_ShouldRestoreStock_WhenCancellingOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        doNothing().when(productService).updateStock(anyLong(), anyInt());
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.updateStatus(1L, OrderStatus.CANCELLED);

        verify(productService, times(1)).updateStock(eq(1L), eq(2));
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should delete order successfully when pending")
    void delete_ShouldDeleteOrder_WhenOrderPending() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        doNothing().when(productService).updateStock(anyLong(), anyInt());
        doNothing().when(orderRepository).deleteById(1L);

        orderService.delete(1L);

        verify(orderRepository, times(1)).findById(1L);
        verify(productService, times(1)).updateStock(eq(1L), eq(2));
        verify(orderRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting processing order")
    void delete_ShouldThrowException_WhenOrderProcessing() {
        order.setStatus(OrderStatus.PROCESSING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.delete(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Cannot delete an order that is already being processed");

        verify(orderRepository, never()).deleteById(anyLong());
    }
}
