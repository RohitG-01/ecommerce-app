package com.ecommerce.controller;

import com.ecommerce.exception.ProductServiceException;
import com.ecommerce.kafka.ProductProducer;
import com.ecommerce.exception.ProductNotFoundException;
import com.ecommerce.model.Product;
import com.ecommerce.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductProducer productProducer;

//    @GetMapping("/test")
//    public String test() {
//        return "Hello, World!";
//    }

    //Creating a product
    @Operation(summary = "createProduct", description = "This method created a product")
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {

        try {
            // Save the product to the database
            Product savedProduct = productService.createProduct(product);

            // Send the saved product to Kafka
            productProducer.sendProduct(savedProduct);

            // Return the saved product
            // ResponseEntity is used to represent the entire HTTP response, including the status code, headers, and body.
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
        } catch (ProductServiceException ex) {
            throw ex; // Propagate the exception to the GlobalExceptionHandler
        } catch (Exception ex) {
            throw new ProductServiceException("Failed to create product: " + ex.getMessage(), ex);
        }
    }


    //Get details of all products
    @Operation(summary = "getAllProducts", description = "This method retrives all products")
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {

        try {
            List<Product> products = productService.getAllProducts();

            if (products.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204 No Content
            }

            return ResponseEntity.ok(products);
        } catch (ProductNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ProductServiceException("Failed to fetch products: " + ex.getMessage(), ex);
        }
    }


    //Get details of a product based on Id
    @Operation(summary = "getProductById", description = "This method retrieves product details based on Id")
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable("id") Long id) {
        try {
            // Fetch the product by ID
            Product product = productService.getProductById(id);

            // If the product is found, return it with 200 OK status
            return ResponseEntity.ok(product);
        } catch (ProductNotFoundException ex) {
            // If the product is not found, return 404 Not Found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception ex) {
            // For any other exceptions, return 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }


    //Update details of a product based on Id
    @Operation(summary = "createProduct", description = "This method updates a product based on Id")
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        try {
            // Attempt to find the product by ID
            Product existingProduct = productService.getProductById(id);

            // Update the product's details
            existingProduct.setName(product.getName());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setDescription(product.getDescription());

            // Save the updated product
            Product updatedProduct = productService.createProduct(existingProduct); // You can reuse the createProduct method as it already handles validation

            // Return the updated product
            return ResponseEntity.ok(updatedProduct);
        } catch (ProductNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Product not found
        } catch (Exception ex) {
            throw new ProductServiceException("Failed to update product with ID: " + id, ex);
        }
    }


    //Delete a product based on Id
    @Operation(summary = "deleteProduct", description = "This method deletes a product based on Id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            // Attempt to find the product by ID
            Product existingProduct = productService.getProductById(id);

            // Delete the product
            productService.deleteProduct(id);

            // Return a response indicating successful deletion
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204 No Content
        } catch (ProductNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Product not found
        } catch (Exception ex) {
            throw new ProductServiceException("Failed to delete product with ID: " + id, ex);
        }
    }
}
