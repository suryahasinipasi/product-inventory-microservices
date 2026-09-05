package com.surya.productservice.service;

import com.surya.productservice.dto.ProductRequest;
import com.surya.productservice.dto.ProductResponse;
import com.surya.productservice.exception.ProductNotFoundException;
import com.surya.productservice.model.Product;
import com.surya.productservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository);
    }

    @Test
    void returnsProductWhenIdExists() {
        Product product = new Product("Laptop", new BigDecimal("999.99"), 10);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponse result = productService.getProductById(1L);

        assertEquals("Laptop", result.name());
        assertEquals(new BigDecimal("999.99"), result.price());
    }

    @Test
    void throwsExceptionWhenProductDoesNotExist() {
        when(productRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> productService.getProductById(100L)
        );
    }

    @Test
    void savesAValidProduct() {
        ProductRequest request = new ProductRequest(
                "Keyboard",
                new BigDecimal("89.99"),
                15
        );

        Product product = new Product(
                request.name(),
                request.price(),
                request.quantity()
        );

        when(productRepository.save(
                org.mockito.ArgumentMatchers.any(Product.class)
        )).thenReturn(product);

        ProductResponse result = productService.addProduct(request);

        assertEquals("Keyboard", result.name());
        verify(productRepository).save(
                org.mockito.ArgumentMatchers.any(Product.class)
        );
    }
}