package com.ecommerce.service;

import com.ecommerce.exception.CartServiceException;
import com.ecommerce.model.Cart;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.Customer;
import com.ecommerce.model.Product;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.CustomerRepository;
import com.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;


    // Helper method to get or create a cart for a customer
    public Cart getOrCreateCart(Long customerId) {
        return cartRepository.findByCustomerId(customerId)
                .orElseGet(() -> {
                    Customer customer = customerRepository.findById(customerId)
                            .orElseThrow(() -> new CartServiceException("Customer not found: " + customerId));
                    Cart newCart = new Cart();
                    newCart.setCustomer(customer);
                    return cartRepository.save(newCart);
                });
    }


    public Cart addToCart(Long customerId, Long productId, int quantity) {
        try {
            validateQuantity(quantity);
            Product product = getProductById(productId);
            Cart cart = getOrCreateCart(customerId);

            // Ensure the cart items list is initialized
            if (cart.getItems() == null) {
                cart.setItems(new ArrayList<>());
            }

            Optional<CartItem> existingItem = cart.getItems().stream()
                    .filter(item -> item.getProductId().equals(productId))
                    .findFirst();

            if (existingItem.isPresent()) {
                // Update existing quantity
                existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
            } else {
                // Add new item to the cart
                cart.getItems().add(new CartItem(productId, quantity));
            }

            return cartRepository.save(cart);
//            return buildCartMessage(product, productId, quantity, customerId, existingItem.isPresent());
        } catch (CartServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new CartServiceException("Failed to add product to cart: " + ex.getMessage(), ex);
        }
    }

    public void removeFromCart(Long customerId, Long productId) {
        try {
            Cart cart = getOrCreateCart(customerId);
            CartItem itemToRemove = findCartItem(cart, productId);

            // Remove the item from the cart
            cart.getItems().remove(itemToRemove);
            cartRepository.save(cart);

        } catch (CartServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new CartServiceException("Failed to remove product from cart: " + ex.getMessage(), ex);
        }
    }

    public void clearCart(Long customerId) {
        try {
            Cart cart = getOrCreateCart(customerId);
            cart.getItems().clear();
            cartRepository.save(cart);
        } catch (Exception ex) {
            throw new CartServiceException("Failed to clear cart: " + ex.getMessage(), ex);
        }
    }


    // Helper method to validate quantity
    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new CartServiceException("Quantity must be greater than 0");
        }
    }

    // Helper method to get product by ID
    private Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new CartServiceException("Product not found: " + productId));
    }


    // Helper method to find a cart item
    private CartItem findCartItem(Cart cart, Long productId) {
        return cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new CartServiceException("Product with ID " + productId +
                        " not found in the cart of customer with ID " + cart.getCustomer().getId()));
    }

}
