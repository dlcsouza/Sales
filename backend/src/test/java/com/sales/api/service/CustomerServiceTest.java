package com.sales.api.service;

import com.sales.api.dto.CustomerDTO;
import com.sales.api.dto.CustomerRequestDTO;
import com.sales.api.entity.Customer;
import com.sales.api.exception.BusinessException;
import com.sales.api.exception.ResourceNotFoundException;
import com.sales.api.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;
    private CustomerRequestDTO customerRequest;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .phone("123456789")
                .address("123 Main St")
                .createdAt(LocalDateTime.now())
                .build();

        customerRequest = CustomerRequestDTO.builder()
                .name("John Doe")
                .email("john@example.com")
                .phone("123456789")
                .address("123 Main St")
                .build();
    }

    @Test
    @DisplayName("Should return all customers")
    void findAll_ShouldReturnAllCustomers() {
        Customer customer2 = Customer.builder()
                .id(2L)
                .name("Jane Doe")
                .email("jane@example.com")
                .build();

        when(customerRepository.findAll()).thenReturn(Arrays.asList(customer, customer2));

        List<CustomerDTO> result = customerService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("John Doe");
        assertThat(result.get(1).getName()).isEqualTo("Jane Doe");
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return customer by ID")
    void findById_ShouldReturnCustomer_WhenCustomerExists() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        CustomerDTO result = customerService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("john@example.com");
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when customer not found")
    void findById_ShouldThrowException_WhenCustomerNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.findById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer not found");

        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should create customer successfully")
    void create_ShouldCreateCustomer_WhenEmailNotExists() {
        when(customerRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerDTO result = customerService.create(customerRequest);

        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("john@example.com");
        verify(customerRepository, times(1)).existsByEmail("john@example.com");
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void create_ShouldThrowException_WhenEmailExists() {
        when(customerRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThatThrownBy(() -> customerService.create(customerRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Email already exists");

        verify(customerRepository, times(1)).existsByEmail("john@example.com");
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should update customer successfully")
    void update_ShouldUpdateCustomer_WhenCustomerExists() {
        CustomerRequestDTO updateRequest = CustomerRequestDTO.builder()
                .name("John Updated")
                .email("john.updated@example.com")
                .phone("987654321")
                .address("456 New St")
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.findByEmail("john.updated@example.com")).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerDTO result = customerService.update(1L, updateRequest);

        assertThat(result).isNotNull();
        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should throw exception when updating with existing email")
    void update_ShouldThrowException_WhenEmailExistsForOtherCustomer() {
        Customer otherCustomer = Customer.builder()
                .id(2L)
                .email("john@example.com")
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.findByEmail("john@example.com")).thenReturn(Optional.of(otherCustomer));

        assertThatThrownBy(() -> customerService.update(1L, customerRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Email already exists");
    }

    @Test
    @DisplayName("Should delete customer successfully")
    void delete_ShouldDeleteCustomer_WhenCustomerExists() {
        when(customerRepository.existsById(1L)).thenReturn(true);
        doNothing().when(customerRepository).deleteById(1L);

        customerService.delete(1L);

        verify(customerRepository, times(1)).existsById(1L);
        verify(customerRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent customer")
    void delete_ShouldThrowException_WhenCustomerNotFound() {
        when(customerRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> customerService.delete(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer not found");

        verify(customerRepository, times(1)).existsById(1L);
        verify(customerRepository, never()).deleteById(anyLong());
    }
}
