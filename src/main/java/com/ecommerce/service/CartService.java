package com.ecommerce.service;

import com.ecommerce.exception.CartServiceException;
import com.ecommerce.model.Cart;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.Product;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    public Cart getCart(Long customerId) {
        try {
            return cartRepository.findByCustomerId(customerId)
                    .orElseGet(() -> {
                        Cart cart = new Cart(); // Use the default constructor
                        cart.setCustomerId(customerId);
                        return cartRepository.save(cart);
                    });
        } catch (Exception ex) {
            throw new CartServiceException("Failed to retrieve cart: " + ex.getMessage(), ex);
        }

    }
    public String addToCart(Long customerId, Long productId, int quantity) {
        try {
            // Validate quantity
            if (quantity <= 0) {
                throw new CartServiceException("Quantity must be greater than 0");
            }

            // Check if the product exists
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new CartServiceException("Product not found: " + productId));

            Cart cart = getCart(customerId);

            // Check if the product already exists in the cart
            Optional<CartItem> existingItem = cart.getItems().stream()
                    .filter(item -> item.getProductId().equals(productId))
                    .findFirst();

            if (existingItem.isPresent()) {
                // Update the quantity if the product is already in the cart
                existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
                cartRepository.save(cart);
                return "Product with ID " + productId + ", quantity updated to " + existingItem.get().getQuantity() + ", in the cart of customer with ID " + customerId;
            } else {
                // Add a new item to the cart
                CartItem newItem = new CartItem();
                newItem.setProductId(productId);
                newItem.setQuantity(quantity);
                cart.getItems().add(newItem);
                cartRepository.save(cart);
                return quantity + " Quantity of " + product.getName() + " with ProductID " + productId + ", added to the cart of customer with ID " + customerId;
            }
        } catch (CartServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new CartServiceException("Failed to add product to cart: " + ex.getMessage(), ex);
        }
    }

    public String removeFromCart(Long customerId, Long productId) {
        try {
            Cart cart = getCart(customerId);

            // Find the item to remove
            Optional<CartItem> itemToRemove = cart.getItems().stream()
                    .filter(item -> item.getProductId().equals(productId))
                    .findFirst();

            if (itemToRemove.isPresent()) {

                // Remove the item from the cart
                cart.getItems().remove(itemToRemove.get());
                cartRepository.save(cart);
                return "Product with ID " + productId + " has been removed from the cart of customer with ID " + customerId;
            } else {
                throw new CartServiceException("Product with ID " + productId + " not found in the cart of customer with ID " + customerId);
            }
        } catch (Exception ex) {
            throw new CartServiceException("Failed to remove product from cart: " + ex.getMessage(), ex);
        }
    }

    public void clearCart(Long customerId) {
        try {
            Cart cart = getCart(customerId);
            cart.getItems().clear();
            cartRepository.save(cart);
        } catch (Exception ex) {
            throw new CartServiceException("Failed to clear cart: " + ex.getMessage(), ex);
        }
    }
}
