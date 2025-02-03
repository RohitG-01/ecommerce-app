package com.ecommerce.controller;

import com.ecommerce.model.Order;
import com.ecommerce.model.OrderItem;
import com.ecommerce.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // Creating an order
    @Operation(summary = "createOrder", description = "Creates an order based on customerId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Order.class))),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PostMapping
    public ResponseEntity<String> createOrder(@RequestParam Long customerId) {
        String message = orderService.createOrder(customerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }


    // Get all orders
    @Operation(summary = "getAllOrders", description = "Retrieves all orders")
    @GetMapping
    public ResponseEntity<String> getAllOrders() {
        String message = orderService.getAllOrders();
        return ResponseEntity.ok(message);
    }


    // Get order by ID
    @Operation(summary = "getOrderById", description = "Retrieves an existing order based on orderId")
    @GetMapping("/{orderId}")
    public ResponseEntity<String> getOrderById(@PathVariable Long orderId) {
        String message = orderService.getOrderById(orderId);
        return ResponseEntity.ok(message);
    }

}
