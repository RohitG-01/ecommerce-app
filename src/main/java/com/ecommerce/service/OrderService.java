package com.ecommerce.service;

import com.ecommerce.exception.OrderNotFoundException;
import com.ecommerce.exception.OrderServiceException;
import com.ecommerce.kafka.OrderProducer;
import com.ecommerce.model.*;
import com.ecommerce.repository.CustomerRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderProducer orderProducer;

    public Order createOrder(Long customerId) {
        try {
            // Fetch the customer entity using customerId
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new OrderServiceException("Customer not found: " + customerId));

            // Get the customer's cart
            Cart cart = cartService.getOrCreateCart(customerId);

            // Validate the cart
            if (cart.getItems().isEmpty()) {
                throw new OrderServiceException("Cannot create an order with an empty cart");
            }

            // Create a new order
            Order order = new Order();
            order.setCustomer(customer);
            order.setStatus("CREATED");

            // Calculate the total amount and add items to the order
            double totalAmount = 0;
            StringBuilder orderDetails = new StringBuilder();
            for (CartItem cartItem : cart.getItems()) {
                Product product = productRepository.findById(cartItem.getProductId())
                        .orElseThrow(() -> new OrderServiceException("Product not found: " + cartItem.getProductId()));

                OrderItem orderItem;
                orderItem = new OrderItem();
                orderItem.setProductId(product.getId());
                orderItem.setProductName(product.getName());
                orderItem.setProductDescription(product.getDescription());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPrice(product.getPrice());
                order.getItems().add(orderItem);

                totalAmount += product.getPrice() * cartItem.getQuantity();
            }
            order.setTotalAmount(totalAmount);

            // Save the order
            Order savedOrder = orderRepository.save(order);

            // Send the order details to Kafka
            orderProducer.sendOrder(savedOrder);

            // Clear the cart
            cartService.clearCart(customerId);

            return savedOrder;
        } catch (OrderServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new OrderServiceException("Failed to create order: " + ex.getMessage(), ex);
        }
    }


    // Get all orders
    public List<Order> getAllOrders() {
        try {

            return orderRepository.findAll();
        } catch (Exception ex) {
            throw new OrderServiceException("Failed to retrieve orders: " + ex.getMessage(), ex);
        }
    }

    // Get order by ID
    public Order getOrderById(Long orderId) {
        try {

            return orderRepository.findById(orderId)
                    .orElseThrow(() -> new OrderServiceException("Order not found with ID: " + orderId));
        } catch (Exception ex) {
            throw new OrderServiceException("Failed to retrieve order: " + ex.getMessage(), ex);
        }
    }

    // Delete order by ID
    public void deleteOrderById(Long orderId) {
        try {
            // Attempt to find the order by ID
            Optional<Order> orderOptional = orderRepository.findById(orderId);

            // If the order is not found, throw an exception
            if (orderOptional.isPresent()) {
                Order order = orderOptional.get();

                // Clear associated order items
                order.getItems().clear();
                orderRepository.save(order); // Persist changes before deletion

                // Delete order
                orderRepository.delete(order);
            } else {
                throw new OrderNotFoundException("Order not found with ID: " + orderId);
            }
        } catch (Exception ex) {
            throw new OrderServiceException("Failed to delete order with ID: " + orderId, ex);
        }
    }

}
