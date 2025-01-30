package com.ecommerce.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Welcome to the Product Service! Use the /products endpoint to create a product.";
    }
}
