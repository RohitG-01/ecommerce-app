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
    public ResponseEntity<Cart> addToCart(
            @RequestParam Long customerId,
            @RequestParam Long productId,
            @RequestParam int quantity) {
            Cart cart = cartService.addToCart(customerId, productId, quantity);
            return ResponseEntity.status(HttpStatus.CREATED).body(cart);
    }


    // Delete cart
    // TO DO
    // Should be able to delete product quantity from the cart instead of entirely deleting the product
    // May be Create a new postMapping for it?
    @Operation(summary = "removeFromCart", description = "Removes product from cart based on customerId and productId")
    @PostMapping("/remove")
    public ResponseEntity<Cart> removeFromCart(
            @RequestParam Long customerId,
            @RequestParam Long productId) {
            cartService.removeFromCart(customerId, productId);
            return ResponseEntity.noContent().build();
    }


    // Clear entire cart endpoint
    @Operation(summary = "clearCart", description = "Clears all products from the cart for the given customerId")
    @DeleteMapping("/clear")
    public ResponseEntity<Cart> clearCart(@RequestParam Long customerId) {
        cartService.clearCart(customerId);
        return ResponseEntity.noContent().build();
    }


    // View cart details endpoint
    @Operation(summary = "getOrCreateCart", description = "Retrieves cart details based on customerId")
    @GetMapping
    public ResponseEntity<Cart> getOrCreateCart(@RequestParam Long customerId) {
        Cart cart = cartService.getOrCreateCart(customerId);
        return ResponseEntity.ok(cart);
    }
}
