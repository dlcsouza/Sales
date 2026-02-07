package com.sales.api.controller;

import com.sales.api.dto.CustomerDTO;
import com.sales.api.dto.CustomerRequestDTO;
import com.sales.api.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Customer management APIs")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    @Operation(summary = "Get all customers")
    public ResponseEntity<List<CustomerDTO>> findAll() {
        return ResponseEntity.ok(customerService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID")
    public ResponseEntity<CustomerDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new customer")
    public ResponseEntity<CustomerDTO> create(@Valid @RequestBody CustomerRequestDTO request) {
        CustomerDTO created = customerService.create(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing customer")
    public ResponseEntity<CustomerDTO> update(@PathVariable Long id, @Valid @RequestBody CustomerRequestDTO request) {
        return ResponseEntity.ok(customerService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a customer")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
