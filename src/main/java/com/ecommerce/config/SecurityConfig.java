package com.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    //Defines the security rules for the application.
    //It customizes the HttpSecurity to apply certain security configurations to incoming HTTP requests.
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF since not needed for stateless API requests
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/products/**").permitAll() // Allow access to /products without authentication
                        .anyRequest().authenticated() // Secure all other endpoints
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Make session stateless
                .httpBasic(Customizer.withDefaults()); // Use Customizer.withDefaults() for HTTP Basic authentication

        return http.build();
    }
}
