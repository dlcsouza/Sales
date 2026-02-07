package com.sales.api.service;

import com.sales.api.dto.ProductDTO;
import com.sales.api.dto.ProductRequestDTO;
import com.sales.api.entity.Product;
import com.sales.api.exception.BusinessException;
import com.sales.api.exception.ResourceNotFoundException;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductRequestDTO productRequest;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("99.99"))
                .stockQuantity(100)
                .createdAt(LocalDateTime.now())
                .build();

        productRequest = ProductRequestDTO.builder()
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("99.99"))
                .stockQuantity(100)
                .build();
    }

    @Test
    @DisplayName("Should return all products")
    void findAll_ShouldReturnAllProducts() {
        Product product2 = Product.builder()
                .id(2L)
                .name("Product 2")
                .price(new BigDecimal("49.99"))
                .stockQuantity(50)
                .build();

        when(productRepository.findAll()).thenReturn(Arrays.asList(product, product2));

        List<ProductDTO> result = productService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Test Product");
        assertThat(result.get(1).getName()).isEqualTo("Product 2");
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return product by ID")
    void findById_ShouldReturnProduct_WhenProductExists() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductDTO result = productService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Product");
        assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("99.99"));
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when product not found")
    void findById_ShouldThrowException_WhenProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found");

        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should search products by name")
    void findByName_ShouldReturnMatchingProducts() {
        when(productRepository.findByNameContainingIgnoreCase("Test"))
                .thenReturn(Arrays.asList(product));

        List<ProductDTO> result = productService.findByName("Test");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Product");
        verify(productRepository, times(1)).findByNameContainingIgnoreCase("Test");
    }

    @Test
    @DisplayName("Should return products in stock")
    void findInStock_ShouldReturnProductsWithStock() {
        when(productRepository.findByStockQuantityGreaterThan(0))
                .thenReturn(Arrays.asList(product));

        List<ProductDTO> result = productService.findInStock();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStockQuantity()).isGreaterThan(0);
        verify(productRepository, times(1)).findByStockQuantityGreaterThan(0);
    }

    @Test
    @DisplayName("Should create product successfully")
    void create_ShouldCreateProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDTO result = productService.create(productRequest);

        assertThat(result.getName()).isEqualTo("Test Product");
        assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("99.99"));
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should update product successfully")
    void update_ShouldUpdateProduct_WhenProductExists() {
        ProductRequestDTO updateRequest = ProductRequestDTO.builder()
                .name("Updated Product")
                .description("Updated Description")
                .price(new BigDecimal("149.99"))
                .stockQuantity(200)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDTO result = productService.update(1L, updateRequest);

        assertThat(result).isNotNull();
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should delete product successfully")
    void delete_ShouldDeleteProduct_WhenProductExists() {
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        productService.delete(1L);

        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent product")
    void delete_ShouldThrowException_WhenProductNotFound() {
        when(productRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> productService.delete(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found");

        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should update stock successfully")
    void updateStock_ShouldUpdateStock_WhenSufficientQuantity() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        productService.updateStock(1L, -50);

        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw exception when insufficient stock")
    void updateStock_ShouldThrowException_WhenInsufficientStock() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.updateStock(1L, -150))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Insufficient stock");

        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
    }
}
