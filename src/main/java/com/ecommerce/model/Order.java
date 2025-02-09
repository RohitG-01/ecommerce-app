package com.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "`order`")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //database auto-increments the primary key value
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonBackReference // Prevents infinite recursion
    private Customer customer;

    private double totalAmount; // Total amount of the order
    private String status; // Status of the order (e.g., "CREATED", "PAID")

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private List<OrderItem> items = new ArrayList<>();

    // Default constructor (required by JPA)
    public Order() {}

    // Parameterized constructor
    public Order(Customer customer, double totalAmount, String status, List<OrderItem> items) {
        this.customer = customer;
        this.totalAmount = totalAmount;
        this.status = status;
        this.items = items;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    // Helper method to add an item to the order
    public void addItem(OrderItem item) {
        this.items.add(item);
    }

    // Helper method to remove an item from the order
    public void removeItem(OrderItem item) {
        this.items.remove(item);
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", customer=" + customer +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                ", items=" + items +
                '}';
    }
}
