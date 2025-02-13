package com.ecommerce.service;

import com.ecommerce.model.Customer;
import com.ecommerce.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

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
        when(customerRepository.save(customer)).thenReturn(customer);

        // Act
        Customer createdCustomer = customerService.createCustomer(customer);

        // Assert
        assertNotNull(createdCustomer);
        assertEquals("John Doe", createdCustomer.getName());
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void getAllCustomers_Success() {
        // Arrange
        when(customerRepository.findAll()).thenReturn(Arrays.asList(customer));

        // Act
        List<Customer> customers = customerService.getAllCustomers();

        // Assert
        assertFalse(customers.isEmpty());
        assertEquals(1, customers.size());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void getCustomerById_Success() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        // Act
        Customer foundCustomer = customerService.getCustomerById(1L);

        // Assert
        assertNotNull(foundCustomer);
        assertEquals("John Doe", foundCustomer.getName());
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    void getCustomerById_NotFound() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> customerService.getCustomerById(1L));
        assertEquals("Customer not found with ID: 1", exception.getMessage());
        verify(customerRepository, times(1)).findById(1L);
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

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(customer)).thenReturn(customer);

        // Act
        Customer result = customerService.updateCustomer(1L, updatedCustomer);

        // Assert
        assertNotNull(result);
        assertEquals("Jane Doe", result.getName());
        assertEquals("jane.doe@example.com", result.getEmail());
        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void deleteCustomer_Success() {
        // Arrange
        doNothing().when(customerRepository).deleteById(1L);

        // Act
        customerService.deleteCustomer(1L);

        // Assert
        verify(customerRepository, times(1)).deleteById(1L);
    }
}