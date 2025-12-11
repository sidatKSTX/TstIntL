package com.harnessdemo.services;

import com.harnessdemo.models.Order;
import com.harnessdemo.models.Order.OrderStatus;
import com.harnessdemo.repositories.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(Long userId, BigDecimal totalAmount) {
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Total amount must be positive");
        }
        Order order = new Order();
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING);
        return orderRepository.save(order);
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        validateStatusTransition(order.getStatus(), newStatus);
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    public Order confirmOrder(Long orderId) {
        return updateOrderStatus(orderId, OrderStatus.CONFIRMED);
    }

    public Order shipOrder(Long orderId) {
        return updateOrderStatus(orderId, OrderStatus.SHIPPED);
    }

    public Order deliverOrder(Long orderId) {
        return updateOrderStatus(orderId, OrderStatus.DELIVERED);
    }

    public Order cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel a delivered order");
        }
        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    public BigDecimal getTotalSpendByUser(Long userId) {
        BigDecimal total = orderRepository.getTotalSpendByUser(userId);
        return total != null ? total : BigDecimal.ZERO;
    }

    public List<Order> getOrdersByDateRange(LocalDateTime start, LocalDateTime end) {
        return orderRepository.findByDateRange(start, end);
    }

    public long countOrdersByStatus(OrderStatus status) {
        return orderRepository.countByStatus(status);
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus next) {
        if (current == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot change status of cancelled order");
        }
        if (current == OrderStatus.DELIVERED && next != OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot change status of delivered order");
        }
    }
}
