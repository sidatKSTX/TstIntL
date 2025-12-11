package com.harnessdemo.services;

import com.harnessdemo.models.Order;
import com.harnessdemo.models.Order.OrderStatus;
import com.harnessdemo.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUserId(100L);
        testOrder.setTotalAmount(new BigDecimal("99.99"));
        testOrder.setStatus(OrderStatus.PENDING);
    }

    // ==================== Create Order Tests (15 tests) ====================

    @Test
    void testCreateOrderSuccess() {
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        Order result = orderService.createOrder(100L, new BigDecimal("99.99"));

        assertNotNull(result);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testCreateOrderWithNullAmount() {
        assertThrows(IllegalArgumentException.class, () ->
            orderService.createOrder(100L, null));
    }

    @Test
    void testCreateOrderWithZeroAmount() {
        assertThrows(IllegalArgumentException.class, () ->
            orderService.createOrder(100L, BigDecimal.ZERO));
    }

    @Test
    void testCreateOrderWithNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () ->
            orderService.createOrder(100L, new BigDecimal("-10.00")));
    }

    @Test
    void testCreateOrderSetsPendingStatus() {
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.createOrder(100L, new BigDecimal("50.00"));

        assertEquals(OrderStatus.PENDING, result.getStatus());
    }

    @Test
    void testCreateOrderStoresUserId() {
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.createOrder(100L, new BigDecimal("50.00"));

        assertEquals(100L, result.getUserId());
    }

    @Test
    void testCreateOrderStoresAmount() {
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.createOrder(100L, new BigDecimal("123.45"));

        assertEquals(new BigDecimal("123.45"), result.getTotalAmount());
    }

    @Test
    void testCreateOrderWithLargeAmount() {
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.createOrder(100L, new BigDecimal("999999.99"));

        assertEquals(new BigDecimal("999999.99"), result.getTotalAmount());
    }

    @Test
    void testCreateOrderWithSmallAmount() {
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.createOrder(100L, new BigDecimal("0.01"));

        assertEquals(new BigDecimal("0.01"), result.getTotalAmount());
    }

    @Test
    void testCreateOrderRepositorySaveCalled() {
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        orderService.createOrder(100L, new BigDecimal("50.00"));

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testCreateOrderWithDecimalPrecision() {
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.createOrder(100L, new BigDecimal("99.999"));

        assertNotNull(result);
    }

    @Test
    void testCreateOrderWithWholeNumber() {
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.createOrder(100L, new BigDecimal("100"));

        assertEquals(new BigDecimal("100"), result.getTotalAmount());
    }

    @Test
    void testCreateOrderForDifferentUsers() {
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order result1 = orderService.createOrder(1L, new BigDecimal("50.00"));
        Order result2 = orderService.createOrder(2L, new BigDecimal("75.00"));

        assertEquals(1L, result1.getUserId());
        assertEquals(2L, result2.getUserId());
    }

    @Test
    void testCreateOrderWithMaxLongUserId() {
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.createOrder(Long.MAX_VALUE, new BigDecimal("50.00"));

        assertEquals(Long.MAX_VALUE, result.getUserId());
    }

    @Test
    void testCreateOrderSaveCalledOnce() {
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        orderService.createOrder(100L, new BigDecimal("50.00"));

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    // ==================== Get Order Tests (15 tests) ====================

    @Test
    void testGetOrderByIdSuccess() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        Optional<Order> result = orderService.getOrderById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void testGetOrderByIdNotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Order> result = orderService.getOrderById(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetOrdersByUserIdSuccess() {
        when(orderRepository.findByUserId(100L)).thenReturn(Arrays.asList(testOrder));

        List<Order> result = orderService.getOrdersByUserId(100L);

        assertEquals(1, result.size());
    }

    @Test
    void testGetOrdersByUserIdEmpty() {
        when(orderRepository.findByUserId(999L)).thenReturn(Collections.emptyList());

        List<Order> result = orderService.getOrdersByUserId(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetOrdersByStatusPending() {
        when(orderRepository.findByStatus(OrderStatus.PENDING)).thenReturn(Arrays.asList(testOrder));

        List<Order> result = orderService.getOrdersByStatus(OrderStatus.PENDING);

        assertEquals(1, result.size());
    }

    @Test
    void testGetOrdersByStatusConfirmed() {
        testOrder.setStatus(OrderStatus.CONFIRMED);
        when(orderRepository.findByStatus(OrderStatus.CONFIRMED)).thenReturn(Arrays.asList(testOrder));

        List<Order> result = orderService.getOrdersByStatus(OrderStatus.CONFIRMED);

        assertEquals(1, result.size());
    }

    @Test
    void testGetOrdersByStatusShipped() {
        testOrder.setStatus(OrderStatus.SHIPPED);
        when(orderRepository.findByStatus(OrderStatus.SHIPPED)).thenReturn(Arrays.asList(testOrder));

        List<Order> result = orderService.getOrdersByStatus(OrderStatus.SHIPPED);

        assertEquals(1, result.size());
    }

    @Test
    void testGetOrdersByStatusDelivered() {
        testOrder.setStatus(OrderStatus.DELIVERED);
        when(orderRepository.findByStatus(OrderStatus.DELIVERED)).thenReturn(Arrays.asList(testOrder));

        List<Order> result = orderService.getOrdersByStatus(OrderStatus.DELIVERED);

        assertEquals(1, result.size());
    }

    @Test
    void testGetOrdersByStatusCancelled() {
        testOrder.setStatus(OrderStatus.CANCELLED);
        when(orderRepository.findByStatus(OrderStatus.CANCELLED)).thenReturn(Arrays.asList(testOrder));

        List<Order> result = orderService.getOrdersByStatus(OrderStatus.CANCELLED);

        assertEquals(1, result.size());
    }

    @Test
    void testGetAllOrders() {
        Order order2 = new Order();
        order2.setId(2L);
        when(orderRepository.findAll()).thenReturn(Arrays.asList(testOrder, order2));

        List<Order> result = orderService.getAllOrders();

        assertEquals(2, result.size());
    }

    @Test
    void testGetAllOrdersEmpty() {
        when(orderRepository.findAll()).thenReturn(Collections.emptyList());

        List<Order> result = orderService.getAllOrders();

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetTotalSpendByUser() {
        when(orderRepository.getTotalSpendByUser(100L)).thenReturn(new BigDecimal("199.98"));

        BigDecimal result = orderService.getTotalSpendByUser(100L);

        assertEquals(new BigDecimal("199.98"), result);
    }

    @Test
    void testGetTotalSpendByUserNull() {
        when(orderRepository.getTotalSpendByUser(999L)).thenReturn(null);

        BigDecimal result = orderService.getTotalSpendByUser(999L);

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void testCountOrdersByStatus() {
        when(orderRepository.countByStatus(OrderStatus.PENDING)).thenReturn(5L);

        long result = orderService.countOrdersByStatus(OrderStatus.PENDING);

        assertEquals(5L, result);
    }

    @Test
    void testGetOrdersByDateRange() {
        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now();
        when(orderRepository.findByDateRange(start, end)).thenReturn(Arrays.asList(testOrder));

        List<Order> result = orderService.getOrdersByDateRange(start, end);

        assertEquals(1, result.size());
    }

    // ==================== Update Order Status Tests (20 tests) ====================

    @Test
    void testConfirmOrderSuccess() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.confirmOrder(1L);

        assertEquals(OrderStatus.CONFIRMED, result.getStatus());
    }

    @Test
    void testConfirmOrderNotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.confirmOrder(999L));
    }

    @Test
    void testShipOrderSuccess() {
        testOrder.setStatus(OrderStatus.CONFIRMED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.shipOrder(1L);

        assertEquals(OrderStatus.SHIPPED, result.getStatus());
    }

    @Test
    void testShipOrderNotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.shipOrder(999L));
    }

    @Test
    void testDeliverOrderSuccess() {
        testOrder.setStatus(OrderStatus.SHIPPED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.deliverOrder(1L);

        assertEquals(OrderStatus.DELIVERED, result.getStatus());
    }

    @Test
    void testDeliverOrderNotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.deliverOrder(999L));
    }

    @Test
    void testCancelOrderSuccess() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.cancelOrder(1L);

        assertEquals(OrderStatus.CANCELLED, result.getStatus());
    }

    @Test
    void testCancelOrderNotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.cancelOrder(999L));
    }

    @Test
    void testCancelDeliveredOrder() {
        testOrder.setStatus(OrderStatus.DELIVERED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        assertThrows(IllegalStateException.class, () -> orderService.cancelOrder(1L));
    }

    @Test
    void testCannotChangeStatusOfCancelledOrder() {
        testOrder.setStatus(OrderStatus.CANCELLED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        assertThrows(IllegalStateException.class, () -> orderService.confirmOrder(1L));
    }

    @Test
    void testCannotChangeStatusOfDeliveredOrder() {
        testOrder.setStatus(OrderStatus.DELIVERED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        assertThrows(IllegalStateException.class, () -> orderService.shipOrder(1L));
    }

    @Test
    void testUpdateOrderStatusSaveCalled() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        orderService.confirmOrder(1L);

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testConfirmOrderFromPending() {
        testOrder.setStatus(OrderStatus.PENDING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.confirmOrder(1L);

        assertEquals(OrderStatus.CONFIRMED, result.getStatus());
    }

    @Test
    void testShipOrderFromConfirmed() {
        testOrder.setStatus(OrderStatus.CONFIRMED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.shipOrder(1L);

        assertEquals(OrderStatus.SHIPPED, result.getStatus());
    }

    @Test
    void testDeliverOrderFromShipped() {
        testOrder.setStatus(OrderStatus.SHIPPED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.deliverOrder(1L);

        assertEquals(OrderStatus.DELIVERED, result.getStatus());
    }

    @Test
    void testCancelPendingOrder() {
        testOrder.setStatus(OrderStatus.PENDING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.cancelOrder(1L);

        assertEquals(OrderStatus.CANCELLED, result.getStatus());
    }

    @Test
    void testCancelConfirmedOrder() {
        testOrder.setStatus(OrderStatus.CONFIRMED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.cancelOrder(1L);

        assertEquals(OrderStatus.CANCELLED, result.getStatus());
    }

    @Test
    void testCancelShippedOrder() {
        testOrder.setStatus(OrderStatus.SHIPPED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.cancelOrder(1L);

        assertEquals(OrderStatus.CANCELLED, result.getStatus());
    }

    @Test
    void testOrderStatusPreservesOtherFields() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.confirmOrder(1L);

        assertEquals(100L, result.getUserId());
        assertEquals(new BigDecimal("99.99"), result.getTotalAmount());
    }

    @Test
    void testMultipleStatusUpdates() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> {
            Order saved = i.getArgument(0);
            testOrder.setStatus(saved.getStatus());
            return testOrder;
        });

        orderService.confirmOrder(1L);
        orderService.shipOrder(1L);
        orderService.deliverOrder(1L);

        assertEquals(OrderStatus.DELIVERED, testOrder.getStatus());
    }
}
