package com.ecommerce.service;

import com.ecommerce.exception.OrderNotFoundException;
import com.ecommerce.exception.OrderServiceException;
import com.ecommerce.model.*;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.CustomerRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.kafka.OrderProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartService cartService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private OrderProducer orderProducer;

    @InjectMocks
    private OrderService orderService;

    private Customer customer;
    private Cart cart;
    private Product product;
    private Order order;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setOrders(new ArrayList<>()); // Initialize the orders list

        cart = new Cart();
        cart.setCustomer(customer);

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(100.0);

        order = new Order();
        order.setId(1L);
        order.setCustomer(customer);
        order.setStatus("CREATED");

        // Add the order to the customer's orders list
        customer.getOrders().add(order);
    }

    @Test
    void createOrder_Success() {
        // Arrange
        CartItem cartItem = new CartItem();
        cartItem.setProductId(1L);
        cartItem.setQuantity(2);
        cart.setItems(Collections.singletonList(cartItem));

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(cartService.getOrCreateCart(1L)).thenReturn(cart);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        doNothing().when(orderProducer).sendOrder(any(Order.class));
        doNothing().when(cartService).clearCart(1L);

        // Act
        Order createdOrder = orderService.createOrder(1L);

        // Assert
        assertNotNull(createdOrder);
        assertEquals("CREATED", createdOrder.getStatus());
        verify(orderProducer, times(1)).sendOrder(any(Order.class));
    }

    @Test
    void createOrder_CustomerNotFound() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderServiceException.class, () -> orderService.createOrder(1L));
    }

    @Test
    void createOrder_EmptyCart() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(cartService.getOrCreateCart(1L)).thenReturn(cart);

        // Act & Assert
        assertThrows(OrderServiceException.class, () -> orderService.createOrder(1L));
    }

    @Test
    void getAllOrders_Success() {
        // Arrange
        when(orderRepository.findAll()).thenReturn(Collections.singletonList(order));

        // Act
        List<Order> orders = orderService.getAllOrders();

        // Assert
        assertFalse(orders.isEmpty());
        assertEquals(1, orders.size());
    }

    @Test
    void getOrderById_Success() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act
        Order foundOrder = orderService.getOrderById(1L);

        // Assert
        assertNotNull(foundOrder);
        assertEquals(1L, foundOrder.getId());
    }

    @Test
    void getOrderById_NotFound() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderServiceException.class, () -> orderService.getOrderById(1L));
    }

    @Test
    void deleteOrderById_Success() {
        // Arrange
        when(orderRepository.existsById(1L)).thenReturn(true);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        doNothing().when(orderRepository).delete(order);

        // Act
        assertDoesNotThrow(() -> orderService.deleteOrderById(1L));

        // Assert
        verify(orderRepository, times(1)).delete(order);
        verify(customerRepository, times(1)).save(customer); // Ensure customer is saved after order removal
    }

    @Test
    void deleteOrderById_NotFound() {
        // Arrange
        when(orderRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> orderService.deleteOrderById(1L));
    }
}