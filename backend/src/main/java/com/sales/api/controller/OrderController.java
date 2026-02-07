package com.sales.api.controller;

import com.sales.api.dto.OrderDTO;
import com.sales.api.dto.OrderRequestDTO;
import com.sales.api.dto.OrderStatusUpdateDTO;
import com.sales.api.entity.OrderStatus;
import com.sales.api.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order management APIs")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "Get all orders")
    public ResponseEntity<List<OrderDTO>> findAll() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<OrderDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get orders by customer ID")
    public ResponseEntity<List<OrderDTO>> findByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(orderService.findByCustomerId(customerId));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get orders by status")
    public ResponseEntity<List<OrderDTO>> findByStatus(@PathVariable OrderStatus status) {
        return ResponseEntity.ok(orderService.findByStatus(status));
    }

    @PostMapping
    @Operation(summary = "Create a new order")
    public ResponseEntity<OrderDTO> create(@Valid @RequestBody OrderRequestDTO request) {
        OrderDTO created = orderService.create(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update order status")
    public ResponseEntity<OrderDTO> updateStatus(@PathVariable Long id, @Valid @RequestBody OrderStatusUpdateDTO request) {
        return ResponseEntity.ok(orderService.updateStatus(id, request.getStatus()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an order")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
