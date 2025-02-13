package com.ecommerce.controller;

import com.ecommerce.exception.OrderNotFoundException;
import com.ecommerce.exception.OrderServiceException;
import com.ecommerce.model.Order;
import com.ecommerce.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(1L);
    }

    @Test
    void createOrder_Success() {
        // Arrange
        when(orderService.createOrder(1L)).thenReturn(order);

        // Act
        ResponseEntity<Order> response = orderController.createOrder(1L);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void getAllOrders_Success() {
        // Arrange
        when(orderService.getAllOrders()).thenReturn(Collections.singletonList(order));

        // Act
        ResponseEntity<List<Order>> response = orderController.getAllOrders();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void getOrderById_Success() {
        // Arrange
        when(orderService.getOrderById(1L)).thenReturn(order);

        // Act
        ResponseEntity<Order> response = orderController.getOrderById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getOrderById_NotFound() {
        // Arrange
        when(orderService.getOrderById(1L)).thenThrow(new OrderNotFoundException("Order not found"));

        // Act
        ResponseEntity<Order> response = orderController.getOrderById(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void deleteOrderById_Success() {
        // Arrange
        doNothing().when(orderService).deleteOrderById(1L);

        // Act
        ResponseEntity<Map<String, String>> response = orderController.deleteOrderById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Order deleted successfully", response.getBody().get("message"));
    }

    @Test
    void deleteOrderById_NotFound() {
        // Arrange
        doThrow(new OrderNotFoundException("Order not found")).when(orderService).deleteOrderById(1L);

        // Act
        ResponseEntity<Map<String, String>> response = orderController.deleteOrderById(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Order not found", response.getBody().get("error"));
    }
}