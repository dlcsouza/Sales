package com.sales.api.service;

import com.sales.api.dto.ProductDTO;
import com.sales.api.dto.ProductRequestDTO;
import com.sales.api.entity.Product;
import com.sales.api.exception.BusinessException;
import com.sales.api.exception.ResourceNotFoundException;
import com.sales.api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<ProductDTO> findAll() {
        return productRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return toDTO(product);
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> findByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> findInStock() {
        return productRepository.findByStockQuantityGreaterThan(0).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductDTO create(ProductRequestDTO request) {
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .build();

        Product saved = productRepository.save(product);
        return toDTO(saved);
    }

    @Transactional
    public ProductDTO update(Long id, ProductRequestDTO request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());

        Product updated = productRepository.save(product);
        return toDTO(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product", "id", id);
        }
        productRepository.deleteById(id);
    }

    @Transactional
    public void updateStock(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        int newStock = product.getStockQuantity() + quantity;
        if (newStock < 0) {
            throw new BusinessException("Insufficient stock for product: " + product.getName());
        }
        product.setStockQuantity(newStock);
        productRepository.save(product);
    }

    private ProductDTO toDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .createdAt(product.getCreatedAt())
                .build();
    }
}
