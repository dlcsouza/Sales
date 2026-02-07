package com.sales.api.controller;

import com.sales.api.dto.ProductDTO;
import com.sales.api.dto.ProductRequestDTO;
import com.sales.api.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product management APIs")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get all products")
    public ResponseEntity<List<ProductDTO>> findAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<ProductDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search products by name")
    public ResponseEntity<List<ProductDTO>> findByName(@RequestParam String name) {
        return ResponseEntity.ok(productService.findByName(name));
    }

    @GetMapping("/in-stock")
    @Operation(summary = "Get products in stock")
    public ResponseEntity<List<ProductDTO>> findInStock() {
        return ResponseEntity.ok(productService.findInStock());
    }

    @PostMapping
    @Operation(summary = "Create a new product")
    public ResponseEntity<ProductDTO> create(@Valid @RequestBody ProductRequestDTO request) {
        ProductDTO created = productService.create(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing product")
    public ResponseEntity<ProductDTO> update(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
