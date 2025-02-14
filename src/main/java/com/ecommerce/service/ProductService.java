package com.ecommerce.service;

import com.ecommerce.exception.ProductNotFoundException;
import com.ecommerce.exception.ProductServiceException;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product createProduct(Product product) {

        try {
            validateProduct(product);   // Validate product data
            return productRepository.save(product);     // Save product to the database
        } catch (Exception ex) {
            // Log the exception and rethrow as a custom exception
            throw new ProductServiceException("Failed to create product: " + ex.getMessage(), ex);
        }
    }

    public List<Product> getAllProducts() {

        try {
            return productRepository.findAll();
        } catch (Exception ex) {
            throw new ProductServiceException("Failed to fetch product list", ex);
        }
    }

    public Product getProductById(Long id) {
        try {
            // Check and fetch product by ID from the database
            Optional<Product> product = productRepository.findById(id);

            // If the product is not found, throw a custom exception
            if (product.isEmpty()) {
                throw new ProductNotFoundException("Product not found with ID: " + id);
            }

            // Return the found product
            return product.get();
        } catch (ProductNotFoundException ex) {
            throw ex; // Propagate the exception to the controller
        } catch (Exception ex) {
            throw new ProductServiceException("Failed to fetch product with ID: " + id, ex);
        }
    }

    public void deleteProduct(Long id) {
        try {
            // Attempt to find the product by ID
            Optional<Product> product = productRepository.findById(id);

            // If the product is not found, throw an exception
            if (product.isEmpty()) {
                throw new ProductNotFoundException("Product not found with ID: " + id);
            }

            // Delete the product from the repository
            productRepository.deleteById(id);
        } catch (ProductNotFoundException ex) {
            throw ex; // Rethrow ProductNotFoundException directly
        } catch (Exception ex) {
            throw new ProductServiceException("Failed to delete product with ID: " + id, ex);
        }
    }

    private void validateProduct(Product product) {
        if (product.getName() == null || product.getName().isEmpty()) {
            throw new ProductServiceException("Product name cannot be empty");
        }
        if (product.getPrice() <= 0) {
            throw new ProductServiceException("Product price must be greater than 0");
        }
    }
}
