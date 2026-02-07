package com.sales.api.service;

import com.sales.api.dto.CustomerDTO;
import com.sales.api.dto.CustomerRequestDTO;
import com.sales.api.entity.Customer;
import com.sales.api.exception.BusinessException;
import com.sales.api.exception.ResourceNotFoundException;
import com.sales.api.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public List<CustomerDTO> findAll() {
        return customerRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CustomerDTO findById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        return toDTO(customer);
    }

    @Transactional
    public CustomerDTO create(CustomerRequestDTO request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists: " + request.getEmail());
        }

        Customer customer = Customer.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .build();

        Customer saved = customerRepository.save(customer);
        return toDTO(saved);
    }

    @Transactional
    public CustomerDTO update(Long id, CustomerRequestDTO request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));

        customerRepository.findByEmail(request.getEmail())
                .filter(c -> !c.getId().equals(id))
                .ifPresent(c -> {
                    throw new BusinessException("Email already exists: " + request.getEmail());
                });

        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());

        Customer updated = customerRepository.save(customer);
        return toDTO(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer", "id", id);
        }
        customerRepository.deleteById(id);
    }

    private CustomerDTO toDTO(Customer customer) {
        return CustomerDTO.builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .createdAt(customer.getCreatedAt())
                .build();
    }
}
