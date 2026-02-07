package com.sales.api.service;

import com.sales.api.dto.*;
import com.sales.api.entity.*;
import com.sales.api.exception.BusinessException;
import com.sales.api.exception.ResourceNotFoundException;
import com.sales.api.repository.CustomerRepository;
import com.sales.api.repository.OrderRepository;
import com.sales.api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;

    @Transactional(readOnly = true)
    public List<OrderDTO> findAll() {
        return orderRepository.findAllWithItems().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderDTO findById(Long id) {
        Order order = orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        return toDTO(order);
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> findByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> findByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderDTO create(OrderRequestDTO request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", request.getCustomerId()));

        Order order = Order.builder()
                .customer(customer)
                .status(OrderStatus.PENDING)
                .build();

        for (OrderItemRequestDTO itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemRequest.getProductId()));

            if (product.getStockQuantity() < itemRequest.getQuantity()) {
                throw new BusinessException("Insufficient stock for product: " + product.getName());
            }

            OrderItem item = OrderItem.builder()
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(product.getPrice())
                    .build();

            order.addItem(item);

            productService.updateStock(product.getId(), -itemRequest.getQuantity());
        }

        order.calculateTotalAmount();
        Order saved = orderRepository.save(order);
        return toDTO(saved);
    }

    @Transactional
    public OrderDTO updateStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException("Cannot update status of a cancelled order");
        }

        if (status == OrderStatus.CANCELLED && order.getStatus() != OrderStatus.CANCELLED) {
            for (OrderItem item : order.getItems()) {
                productService.updateStock(item.getProduct().getId(), item.getQuantity());
            }
        }

        order.setStatus(status);
        Order updated = orderRepository.save(order);
        return toDTO(updated);
    }

    @Transactional
    public void delete(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CANCELLED) {
            throw new BusinessException("Cannot delete an order that is already being processed");
        }

        if (order.getStatus() != OrderStatus.CANCELLED) {
            for (OrderItem item : order.getItems()) {
                productService.updateStock(item.getProduct().getId(), item.getQuantity());
            }
        }

        orderRepository.deleteById(id);
    }

    private OrderDTO toDTO(Order order) {
        List<OrderItemDTO> itemDTOs = order.getItems().stream()
                .map(item -> OrderItemDTO.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .subtotal(item.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        return OrderDTO.builder()
                .id(order.getId())
                .customerId(order.getCustomer().getId())
                .customerName(order.getCustomer().getName())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .items(itemDTOs)
                .build();
    }
}
