package com.ecommerce.controller;

import com.ecommerce.model.Cart;
import com.ecommerce.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // Add to cart endpoint
    @Operation(summary = "addToCart", description = "Adds product to cart based on customerId, productId and Quantity")
    @PostMapping("/add")
    public ResponseEntity<String> addToCart(
            @RequestParam Long customerId,
            @RequestParam Long productId,
            @RequestParam int quantity) {
            String message = cartService.addToCart(customerId, productId, quantity);
            return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }


    // Delete cart
    // TO DO
    // Should be able to delete product quantity from the cart instead of entirely deleting the product
    // May be Create a new postMapping for it?
    @Operation(summary = "removeFromCart", description = "Removes product from cart based on customerId and productId")
    @PostMapping("/remove")
    public ResponseEntity<String> removeFromCart(
            @RequestParam Long customerId,
            @RequestParam Long productId) {
        String message = cartService.removeFromCart(customerId, productId);
        return ResponseEntity.ok(message);
    }


    // View cart details endpoint
    @Operation(summary = "getCart", description = "Retrieves cart details based on customerId")
    @GetMapping
    public ResponseEntity<Cart> getCart(@RequestParam Long customerId) {
        Cart cart = cartService.getCart(customerId);
        return ResponseEntity.ok(cart);
    }
}
