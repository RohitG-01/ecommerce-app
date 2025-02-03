package com.ecommerce.service;

import com.ecommerce.exception.OrderServiceException;
import com.ecommerce.kafka.OrderProducer;
import com.ecommerce.model.*;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderProducer orderProducer;

    public String createOrder(Long customerId) {
        try {
            // Get the customer's cart
            Cart cart = cartService.getCart(customerId);

            // Validate the cart
            if (cart.getItems().isEmpty()) {
                throw new OrderServiceException("Cannot create an order with an empty cart");
            }

            // Create a new order
            Order order = new Order();
            order.setCustomerId(customerId);
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
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPrice(product.getPrice());
                order.getItems().add(orderItem);

                totalAmount += product.getPrice() * cartItem.getQuantity();

                // Add product details to the message
                orderDetails.append("\n- Product ID: ").append(product.getId())
                        .append(", Name: ").append(product.getName())
                        .append(", Quantity: ").append(cartItem.getQuantity())
                        .append(", Price: ").append(product.getPrice());
            }
            order.setTotalAmount(totalAmount);

            // Save the order
            Order savedOrder = orderRepository.save(order);

            // Send the order details to Kafka
            orderProducer.sendOrder(savedOrder);

            // Clear the cart
            cartService.clearCart(customerId);

            // Build the success message
            String message = "Order created successfully for customer with ID " + customerId +
                    "\nOrder ID: " + savedOrder.getId() +
                    "\nTotal Amount: " + totalAmount +
                    "\nProducts:" + orderDetails.toString();

            return message;
        } catch (OrderServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new OrderServiceException("Failed to create order: " + ex.getMessage(), ex);
        }
    }


    // Get all orders
    public String getAllOrders() {
        try {
            List<Order> orders = orderRepository.findAll();
            if (orders.isEmpty()) {
                return "No orders found.";
            } else {
                StringBuilder message = new StringBuilder("Total orders: " + orders.size() + "\n\n");
                for (Order order : orders) {
                    message.append("Order ID: ").append(order.getId())
                            .append(", Customer ID: ").append(order.getCustomerId())
                            .append(", Total Amount: ").append(order.getTotalAmount())
                            .append(", Status: ").append(order.getStatus())
                            .append("\n");
                }
                return message.toString();
            }
        } catch (Exception ex) {
            throw new OrderServiceException("Failed to retrieve orders: " + ex.getMessage(), ex);
        }
    }

    // Get order by ID
    public String getOrderById(Long orderId) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new OrderServiceException("Order not found with ID: " + orderId));

            StringBuilder message = new StringBuilder("Order details for Order ID: " + orderId + "\n");
            message.append("Customer ID: ").append(order.getCustomerId()).append("\n");
            message.append("Total Amount: ").append(order.getTotalAmount()).append("\n");
            message.append("Status: ").append(order.getStatus()).append("\n");
            message.append("Products:\n");

            for (OrderItem item : order.getItems()) {
                message.append("- Product ID: ").append(item.getProductId())
                        .append(", Quantity: ").append(item.getQuantity())
                        .append(", Price: ").append(item.getPrice()).append("\n");
            }

            return message.toString();
        } catch (Exception ex) {
            throw new OrderServiceException("Failed to retrieve order: " + ex.getMessage(), ex);
        }
    }

}
