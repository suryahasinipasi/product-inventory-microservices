package com.surya.productservice.config;

import com.surya.productservice.model.Product;
import com.surya.productservice.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedProducts(ProductRepository productRepository) {
        return args -> {
            if (productRepository.count() == 0) {
                productRepository.saveAll(List.of(
                        new Product("Laptop", new BigDecimal("999.99"), 10),
                        new Product("Phone", new BigDecimal("699.99"), 20),
                        new Product("Headphones", new BigDecimal("149.99"), 30),
                        new Product("Smart Watch", new BigDecimal("199.99"), 15),
                        new Product("Keyboard", new BigDecimal("89.99"), 25),
                        new Product("Wireless Mouse", new BigDecimal("39.99"), 40),
                        new Product("Tablet", new BigDecimal("449.99"), 12)
                ));
            }
        };
    }
}
