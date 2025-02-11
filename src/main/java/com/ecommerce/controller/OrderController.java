package com.ecommerce.controller;

import com.ecommerce.exception.OrderNotFoundException;
import com.ecommerce.exception.OrderServiceException;
import com.ecommerce.model.Customer;
import com.ecommerce.model.Order;
import com.ecommerce.repository.CustomerRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.service.CustomerService;
import com.ecommerce.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    // Creating an order
    @Operation(summary = "createOrder", description = "Creates an order based on customerId")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "201", description = "Order created successfully",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = Order.class))),
//            @ApiResponse(responseCode = "400", description = "Bad request")
//    })
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestParam Long customerId) {
        Order order = orderService.createOrder(customerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }


    // Get all orders
    @Operation(summary = "getAllOrders", description = "Retrieves all orders")
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }


    // Get order by ID
    @Operation(summary = "getOrderById", description = "Retrieves an existing order based on orderId")
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }


    //Delete an order based on ID
    @Operation(summary = "deleteOrderById", description = "Deletes a order based on orderId")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Map<String, String>> deleteOrderById(@PathVariable Long orderId) {
        try {
            // Attempt to find the order by ID
            Order order = orderService.getOrderById(orderId);

            // Delete the order
            orderService.deleteOrderById(orderId);

            // Explicitly return 200 OK with success message
            Map<String, String> response = new HashMap<>();
            response.put("message", "Order deleted successfully");
            return ResponseEntity.ok(response); // 200 OK

        } catch (OrderNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Order not found")); // Order not found
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete order with ID: " + orderId));
        }
    }

}
