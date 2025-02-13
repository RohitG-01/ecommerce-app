package com.ecommerce.controller;

import com.ecommerce.exception.ProductNotFoundException;
import com.ecommerce.exception.ProductServiceException;
import com.ecommerce.model.Product;
import com.ecommerce.service.ProductService;
import com.ecommerce.kafka.ProductProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private ProductProducer productProducer;

    @InjectMocks
    private ProductController productController;

    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(100.0);
        product.setDescription("Test Description");
    }

    @Test
    void testCreateProduct_Success() {
        // Arrange
        when(productService.createProduct(product)).thenReturn(product);

        // Act
        ResponseEntity<Product> response = productController.createProduct(product);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Product", response.getBody().getName());
        verify(productProducer, times(1)).sendProduct(product);
    }

    @Test
    void testCreateProduct_Failure() {
        // Arrange
        when(productService.createProduct(product)).thenThrow(new ProductServiceException("Failed to create product"));

        // Act & Assert
        assertThrows(ProductServiceException.class, () -> productController.createProduct(product));
        verify(productProducer, never()).sendProduct(any());
    }

    @Test
    void testGetAllProducts_Success() {
        // Arrange
        Product product1 = new Product();
        product1.setName("Product 1");
        Product product2 = new Product();
        product2.setName("Product 2");

        when(productService.getAllProducts()).thenReturn(Arrays.asList(product1, product2));

        // Act
        ResponseEntity<List<Product>> response = productController.getAllProducts();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testGetAllProducts_Empty() {
        // Arrange
        when(productService.getAllProducts()).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<Product>> response = productController.getAllProducts();

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetProductById_Success() {
        // Arrange
        when(productService.getProductById(1L)).thenReturn(product);

        // Act
        ResponseEntity<Product> response = productController.getProductById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Product", response.getBody().getName());
    }

    @Test
    void testGetProductById_NotFound() {
        // Arrange
        when(productService.getProductById(1L)).thenThrow(new ProductNotFoundException("Product not found"));

        // Act
        ResponseEntity<Product> response = productController.getProductById(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testUpdateProduct_Success() {
        // Arrange
        when(productService.getProductById(1L)).thenReturn(product);
        when(productService.createProduct(product)).thenReturn(product);

        // Act
        ResponseEntity<Product> response = productController.updateProduct(1L, product);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Product", response.getBody().getName());
    }

    @Test
    void testUpdateProduct_NotFound() {
        // Arrange
        when(productService.getProductById(1L)).thenThrow(new ProductNotFoundException("Product not found"));

        // Act
        ResponseEntity<Product> response = productController.updateProduct(1L, product);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testDeleteProduct_Success() {
        // Arrange
        when(productService.getProductById(1L)).thenReturn(product);
        doNothing().when(productService).deleteProduct(1L);

        // Act
        ResponseEntity<Void> response = productController.deleteProduct(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    void testDeleteProduct_NotFound() {
        // Arrange
        when(productService.getProductById(1L)).thenThrow(new ProductNotFoundException("Product not found"));

        // Act
        ResponseEntity<Void> response = productController.deleteProduct(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(productService, never()).deleteProduct(1L);
    }
}