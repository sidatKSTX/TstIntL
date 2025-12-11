package com.harnessdemo.controllers;

import com.harnessdemo.models.Order;
import com.harnessdemo.models.Order.OrderStatus;
import com.harnessdemo.services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUserId(100L);
        testOrder.setTotalAmount(new BigDecimal("99.99"));
        testOrder.setStatus(OrderStatus.PENDING);
    }

    // ==================== Create Order Tests (6 tests) ====================

    @Test
    void testCreateOrderSuccess() {
        when(orderService.createOrder(anyLong(), any(BigDecimal.class))).thenReturn(testOrder);

        ResponseEntity<Order> response = orderController.createOrder(
            new OrderController.CreateOrderRequest(100L, new BigDecimal("99.99")));

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testCreateOrderReturnsOrder() {
        when(orderService.createOrder(anyLong(), any(BigDecimal.class))).thenReturn(testOrder);

        ResponseEntity<Order> response = orderController.createOrder(
            new OrderController.CreateOrderRequest(100L, new BigDecimal("99.99")));

        assertNotNull(response.getBody());
        assertEquals(100L, response.getBody().getUserId());
    }

    @Test
    void testCreateOrderCallsService() {
        when(orderService.createOrder(anyLong(), any(BigDecimal.class))).thenReturn(testOrder);

        orderController.createOrder(new OrderController.CreateOrderRequest(100L, new BigDecimal("50.00")));

        verify(orderService).createOrder(100L, new BigDecimal("50.00"));
    }

    @Test
    void testCreateOrderWithAmount() {
        when(orderService.createOrder(anyLong(), any(BigDecimal.class))).thenReturn(testOrder);

        ResponseEntity<Order> response = orderController.createOrder(
            new OrderController.CreateOrderRequest(100L, new BigDecimal("99.99")));

        assertEquals(new BigDecimal("99.99"), response.getBody().getTotalAmount());
    }

    @Test
    void testCreateOrderStatusPending() {
        when(orderService.createOrder(anyLong(), any(BigDecimal.class))).thenReturn(testOrder);

        ResponseEntity<Order> response = orderController.createOrder(
            new OrderController.CreateOrderRequest(100L, new BigDecimal("99.99")));

        assertEquals(OrderStatus.PENDING, response.getBody().getStatus());
    }

    @Test
    void testCreateOrderWithDifferentUser() {
        Order userOrder = new Order();
        userOrder.setUserId(200L);
        when(orderService.createOrder(eq(200L), any(BigDecimal.class))).thenReturn(userOrder);

        ResponseEntity<Order> response = orderController.createOrder(
            new OrderController.CreateOrderRequest(200L, new BigDecimal("75.00")));

        assertEquals(200L, response.getBody().getUserId());
    }

    // ==================== Get Order Tests (6 tests) ====================

    @Test
    void testGetOrderByIdSuccess() {
        when(orderService.getOrderById(1L)).thenReturn(Optional.of(testOrder));

        ResponseEntity<Order> response = orderController.getOrderById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetOrderByIdNotFound() {
        when(orderService.getOrderById(999L)).thenReturn(Optional.empty());

        ResponseEntity<Order> response = orderController.getOrderById(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetAllOrders() {
        when(orderService.getAllOrders()).thenReturn(Arrays.asList(testOrder));

        ResponseEntity<List<Order>> response = orderController.getAllOrders();

        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetOrdersByUser() {
        when(orderService.getOrdersByUserId(100L)).thenReturn(Arrays.asList(testOrder));

        ResponseEntity<List<Order>> response = orderController.getOrdersByUser(100L);

        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetOrdersByStatus() {
        when(orderService.getOrdersByStatus(OrderStatus.PENDING)).thenReturn(Arrays.asList(testOrder));

        ResponseEntity<List<Order>> response = orderController.getOrdersByStatus(OrderStatus.PENDING);

        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetUserTotalSpend() {
        when(orderService.getTotalSpendByUser(100L)).thenReturn(new BigDecimal("199.98"));

        ResponseEntity<BigDecimal> response = orderController.getUserTotalSpend(100L);

        assertEquals(new BigDecimal("199.98"), response.getBody());
    }

    // ==================== Order Status Update Tests (6 tests) ====================

    @Test
    void testConfirmOrderSuccess() {
        testOrder.setStatus(OrderStatus.CONFIRMED);
        when(orderService.confirmOrder(1L)).thenReturn(testOrder);

        ResponseEntity<Order> response = orderController.confirmOrder(1L);

        assertEquals(OrderStatus.CONFIRMED, response.getBody().getStatus());
    }

    @Test
    void testShipOrderSuccess() {
        testOrder.setStatus(OrderStatus.SHIPPED);
        when(orderService.shipOrder(1L)).thenReturn(testOrder);

        ResponseEntity<Order> response = orderController.shipOrder(1L);

        assertEquals(OrderStatus.SHIPPED, response.getBody().getStatus());
    }

    @Test
    void testDeliverOrderSuccess() {
        testOrder.setStatus(OrderStatus.DELIVERED);
        when(orderService.deliverOrder(1L)).thenReturn(testOrder);

        ResponseEntity<Order> response = orderController.deliverOrder(1L);

        assertEquals(OrderStatus.DELIVERED, response.getBody().getStatus());
    }

    @Test
    void testCancelOrderSuccess() {
        testOrder.setStatus(OrderStatus.CANCELLED);
        when(orderService.cancelOrder(1L)).thenReturn(testOrder);

        ResponseEntity<Order> response = orderController.cancelOrder(1L);

        assertEquals(OrderStatus.CANCELLED, response.getBody().getStatus());
    }

    @Test
    void testConfirmOrderCallsService() {
        when(orderService.confirmOrder(1L)).thenReturn(testOrder);

        orderController.confirmOrder(1L);

        verify(orderService).confirmOrder(1L);
    }

    @Test
    void testCancelOrderCallsService() {
        when(orderService.cancelOrder(1L)).thenReturn(testOrder);

        orderController.cancelOrder(1L);

        verify(orderService).cancelOrder(1L);
    }

    // ==================== Additional Tests (12 tests) ====================

    @Test
    void testGetAllOrdersEmpty() {
        when(orderService.getAllOrders()).thenReturn(Collections.emptyList());

        ResponseEntity<List<Order>> response = orderController.getAllOrders();

        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testGetOrdersByUserEmpty() {
        when(orderService.getOrdersByUserId(999L)).thenReturn(Collections.emptyList());

        ResponseEntity<List<Order>> response = orderController.getOrdersByUser(999L);

        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testGetOrdersByStatusEmpty() {
        when(orderService.getOrdersByStatus(OrderStatus.DELIVERED)).thenReturn(Collections.emptyList());

        ResponseEntity<List<Order>> response = orderController.getOrdersByStatus(OrderStatus.DELIVERED);

        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testGetUserTotalSpendZero() {
        when(orderService.getTotalSpendByUser(999L)).thenReturn(BigDecimal.ZERO);

        ResponseEntity<BigDecimal> response = orderController.getUserTotalSpend(999L);

        assertEquals(BigDecimal.ZERO, response.getBody());
    }

    @Test
    void testShipOrderCallsService() {
        when(orderService.shipOrder(1L)).thenReturn(testOrder);

        orderController.shipOrder(1L);

        verify(orderService).shipOrder(1L);
    }

    @Test
    void testDeliverOrderCallsService() {
        when(orderService.deliverOrder(1L)).thenReturn(testOrder);

        orderController.deliverOrder(1L);

        verify(orderService).deliverOrder(1L);
    }

    @Test
    void testGetOrderByIdReturnsCorrectOrder() {
        when(orderService.getOrderById(1L)).thenReturn(Optional.of(testOrder));

        ResponseEntity<Order> response = orderController.getOrderById(1L);

        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void testGetAllOrdersReturnsMultiple() {
        Order order2 = new Order();
        order2.setId(2L);
        when(orderService.getAllOrders()).thenReturn(Arrays.asList(testOrder, order2));

        ResponseEntity<List<Order>> response = orderController.getAllOrders();

        assertEquals(2, response.getBody().size());
    }

    @Test
    void testConfirmOrderReturnsOkStatus() {
        when(orderService.confirmOrder(1L)).thenReturn(testOrder);

        ResponseEntity<Order> response = orderController.confirmOrder(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testShipOrderReturnsOkStatus() {
        when(orderService.shipOrder(1L)).thenReturn(testOrder);

        ResponseEntity<Order> response = orderController.shipOrder(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDeliverOrderReturnsOkStatus() {
        when(orderService.deliverOrder(1L)).thenReturn(testOrder);

        ResponseEntity<Order> response = orderController.deliverOrder(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testCancelOrderReturnsOkStatus() {
        when(orderService.cancelOrder(1L)).thenReturn(testOrder);

        ResponseEntity<Order> response = orderController.cancelOrder(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
