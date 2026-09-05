package com.surya.orderservice.service;

import com.surya.orderservice.dto.CreateOrderRequest;
import com.surya.orderservice.dto.OrderItemResponse;
import com.surya.orderservice.dto.OrderResponse;
import com.surya.orderservice.model.CustomerOrder;
import com.surya.orderservice.model.OrderItem;
import com.surya.orderservice.repository.OrderRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public OrderResponse createOrder(CreateOrderRequest request) {
        CustomerOrder order = new CustomerOrder(
                request.customerName(),
                request.customerEmail()
        );

        request.items().forEach(item ->
                order.addItem(new OrderItem(
                        item.productId(),
                        item.productName(),
                        item.unitPrice(),
                        item.quantity()
                ))
        );

        CustomerOrder savedOrder = orderRepository.save(order);
        return toResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository
                .findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        CustomerOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Order not found: " + id
                ));

        return toResponse(order);
    }

    private OrderResponse toResponse(CustomerOrder order) {
        List<OrderItemResponse> items = order.getItems()
                .stream()
                .map(item -> new OrderItemResponse(
                        item.getProductId(),
                        item.getProductName(),
                        item.getUnitPrice(),
                        item.getQuantity()
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getCustomerName(),
                order.getCustomerEmail(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                items
        );
    }
}
