package com.ecommerce.service;

import com.ecommerce.exception.ProductNotFoundException;
import com.ecommerce.exception.ProductServiceException;
import com.ecommerce.model.Product;
import com.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateProduct_Success() {
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(100.0);
        product.setDescription("Test Description");

        when(productRepository.save(product)).thenReturn(product);

        Product savedProduct = productService.createProduct(product);

        assertNotNull(savedProduct);
        assertEquals("Test Product", savedProduct.getName());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testCreateProduct_InvalidName() {
        Product product = new Product();
        product.setName("");
        product.setPrice(100.0);

        assertThrows(ProductServiceException.class, () -> productService.createProduct(product));
    }

    @Test
    void testCreateProduct_InvalidPrice() {
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(0.0);

        assertThrows(ProductServiceException.class, () -> productService.createProduct(product));
    }

    @Test
    void testGetAllProducts_Success() {
        Product product1 = new Product();
        product1.setName("Product 1");
        Product product2 = new Product();
        product2.setName("Product 2");

        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        List<Product> products = productService.getAllProducts();

        assertNotNull(products);
        assertEquals(2, products.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testGetProductById_Success() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product foundProduct = productService.getProductById(1L);

        assertNotNull(foundProduct);
        assertEquals("Test Product", foundProduct.getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProductById_NotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(1L));
    }

    @Test
    void testDeleteProduct_Success() {
        Product product = new Product();
        product.setId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).deleteById(1L);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteProduct_NotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(1L));
    }
}