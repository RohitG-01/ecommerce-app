package com.ecommerce.controller;

import com.ecommerce.model.Customer;
import com.ecommerce.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");
        customer.setPassword("password123");
        customer.setPhoneNumber("1234567890");
        customer.setAddress("123 Main St");
    }

    @Test
    void createCustomer_Success() {
        // Arrange
        when(customerService.createCustomer(customer)).thenReturn(customer);

        // Act
        ResponseEntity<Customer> response = customerController.createCustomer(customer);

        // Assert
        assertNotNull(response.getBody());
        assertEquals("John Doe", response.getBody().getName());
        assertEquals(200, response.getStatusCodeValue());
        verify(customerService, times(1)).createCustomer(customer);
    }

    @Test
    void getAllCustomers_Success() {
        // Arrange
        when(customerService.getAllCustomers()).thenReturn(Arrays.asList(customer));

        // Act
        ResponseEntity<List<Customer>> response = customerController.getAllCustomers();

        // Assert
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().size());
        assertEquals(200, response.getStatusCodeValue());
        verify(customerService, times(1)).getAllCustomers();
    }

    @Test
    void getCustomerById_Success() {
        // Arrange
        when(customerService.getCustomerById(1L)).thenReturn(customer);

        // Act
        ResponseEntity<Customer> response = customerController.getCustomerById(1L);

        // Assert
        assertNotNull(response.getBody());
        assertEquals("John Doe", response.getBody().getName());
        assertEquals(200, response.getStatusCodeValue());
        verify(customerService, times(1)).getCustomerById(1L);
    }

    @Test
    void updateCustomer_Success() {
        // Arrange
        Customer updatedCustomer = new Customer();
        updatedCustomer.setName("Jane Doe");
        updatedCustomer.setEmail("jane.doe@example.com");
        updatedCustomer.setPassword("newpassword123");
        updatedCustomer.setPhoneNumber("0987654321");
        updatedCustomer.setAddress("456 Elm St");

        when(customerService.updateCustomer(1L, updatedCustomer)).thenReturn(customer);

        // Act
        ResponseEntity<Customer> response = customerController.updateCustomer(1L, updatedCustomer);

        // Assert
        assertNotNull(response.getBody());
        assertEquals("John Doe", response.getBody().getName());
        assertEquals(200, response.getStatusCodeValue());
        verify(customerService, times(1)).updateCustomer(1L, updatedCustomer);
    }

    @Test
    void deleteCustomer_Success() {
        // Arrange
        doNothing().when(customerService).deleteCustomer(1L);

        // Act
        ResponseEntity<Void> response = customerController.deleteCustomer(1L);

        // Assert
        assertEquals(204, response.getStatusCodeValue());
        verify(customerService, times(1)).deleteCustomer(1L);
    }
}