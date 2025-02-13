package com.ecommerce.service;

import com.ecommerce.exception.CartServiceException;
import com.ecommerce.model.Cart;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.Customer;
import com.ecommerce.model.Product;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.CustomerRepository;
import com.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartService cartService;

    private Customer customer;
    private Product product;
    private Cart cart;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(100.0);

        cart = new Cart();
        cart.setCustomer(customer);
        cart.setItems(new ArrayList<>());

        cartItem = new CartItem(1L, 2);
    }

    @Test
    void getOrCreateCart_ExistingCart() {
        // Arrange
        when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.of(cart));

        // Act
        Cart result = cartService.getOrCreateCart(1L);

        // Assert
        assertNotNull(result);
        assertEquals(customer, result.getCustomer());
        verify(cartRepository, times(1)).findByCustomerId(1L);
        verify(customerRepository, never()).findById(anyLong());
    }

    @Test
    void getOrCreateCart_NewCart() {
        // Arrange
        when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.empty());
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // Act
        Cart result = cartService.getOrCreateCart(1L);

        // Assert
        assertNotNull(result);
        assertEquals(customer, result.getCustomer());
        verify(cartRepository, times(1)).findByCustomerId(1L);
        verify(customerRepository, times(1)).findById(1L);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void addToCart_NewItem() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(cart)).thenReturn(cart);

        // Act
        Cart result = cartService.addToCart(1L, 1L, 2);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(2, result.getItems().get(0).getQuantity());
        verify(productRepository, times(1)).findById(1L);
        verify(cartRepository, times(1)).findByCustomerId(1L);
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void addToCart_ExistingItem() {
        // Arrange
        cart.getItems().add(cartItem);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(cart)).thenReturn(cart);

        // Act
        Cart result = cartService.addToCart(1L, 1L, 3);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(5, result.getItems().get(0).getQuantity());
        verify(productRepository, times(1)).findById(1L);
        verify(cartRepository, times(1)).findByCustomerId(1L);
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void addToCart_InvalidQuantity() {
        // Act & Assert
        assertThrows(CartServiceException.class, () -> cartService.addToCart(1L, 1L, 0));
    }

    @Test
    void removeFromCart_Success() {
        // Arrange
        cart.getItems().add(cartItem);

        when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(cart)).thenReturn(cart);

        // Act
        cartService.removeFromCart(1L, 1L);

        // Assert
        assertTrue(cart.getItems().isEmpty());
        verify(cartRepository, times(1)).findByCustomerId(1L);
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void removeFromCart_ItemNotFound() {
        // Arrange
        when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.of(cart));

        // Act & Assert
        assertThrows(CartServiceException.class, () -> cartService.removeFromCart(1L, 1L));
    }

    @Test
    void clearCart_Success() {
        // Arrange
        cart.getItems().add(cartItem);

        when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.of(cart));

        // Act
        cartService.clearCart(1L);

        // Assert
        assertTrue(cart.getItems().isEmpty());
        verify(cartRepository, times(1)).findByCustomerId(1L);
        verify(cartRepository, times(1)).save(cart);
        verify(cartRepository, times(1)).delete(cart);
    }
}