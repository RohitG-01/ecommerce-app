package com.ecommerce.controller;

import com.ecommerce.model.Cart;
import com.ecommerce.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    private Cart cart;

    @BeforeEach
    void setUp() {
        cart = new Cart();
    }

    @Test
    void addToCart_Success() {
        // Arrange
        when(cartService.addToCart(1L, 1L, 2)).thenReturn(cart);

        // Act
        ResponseEntity<Cart> response = cartController.addToCart(1L, 1L, 2);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(cartService, times(1)).addToCart(1L, 1L, 2);
    }

    @Test
    void removeFromCart_Success() {
        // Arrange
        doNothing().when(cartService).removeFromCart(1L, 1L);

        // Act
        ResponseEntity<Cart> response = cartController.removeFromCart(1L, 1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(cartService, times(1)).removeFromCart(1L, 1L);
    }

    @Test
    void clearCart_Success() {
        // Arrange
        doNothing().when(cartService).clearCart(1L);

        // Act
        ResponseEntity<Cart> response = cartController.clearCart(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(cartService, times(1)).clearCart(1L);
    }

    @Test
    void getOrCreateCart_Success() {
        // Arrange
        when(cartService.getOrCreateCart(1L)).thenReturn(cart);

        // Act
        ResponseEntity<Cart> response = cartController.getOrCreateCart(1L);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(cartService, times(1)).getOrCreateCart(1L);
    }
}