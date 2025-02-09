package com.ecommerce.controller;

import com.ecommerce.model.Customer;
import com.ecommerce.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // Create a new customer
    @Operation(summary = "createCustomer", description = "Creates a customer")
    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        Customer createdCustomer = customerService.createCustomer(customer);
        return ResponseEntity.ok(createdCustomer);
    }

    // Get all customers
    @Operation(summary = "getAllCustomers", description = "Retrieves all customers")
    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    // Get a customer by ID
    @Operation(summary = "getCustomerById", description = "Retrieves customer by Id")
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        Customer customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }

    // Update a customer
    @Operation(summary = "updateCustomer", description = "Updates customer by Id")
    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer updatedCustomer) {
        Customer customer = customerService.updateCustomer(id, updatedCustomer);
        return ResponseEntity.ok(customer);
    }

    // Delete a customer
    @Operation(summary = "deleteCustomer", description = "Deletes customer by Id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

}
