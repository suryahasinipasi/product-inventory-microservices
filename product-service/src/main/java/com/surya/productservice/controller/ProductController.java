package com.surya.productservice.controller;

import com.surya.productservice.analytics.ProductViewCounter;
import com.surya.productservice.dto.ProductRequest;
import com.surya.productservice.dto.ProductResponse;
import com.surya.productservice.event.ProductEvent;
import com.surya.productservice.messaging.ProductEventProducer;
import com.surya.productservice.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http:" + "//localhost:4200")
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ProductViewCounter productViewCounter;
    private final ProductEventProducer productEventProducer;

    public ProductController(
            ProductService productService,
            ProductViewCounter productViewCounter,
            ProductEventProducer productEventProducer) {

        this.productService = productService;
        this.productViewCounter = productViewCounter;
        this.productEventProducer = productEventProducer;
    }

    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/filter")
    public List<ProductResponse> filterProducts(
            @RequestParam BigDecimal maxPrice) {

        return productService.getProductsByMaximumPrice(maxPrice);
    }

    @GetMapping("/views")
    public Map<Long, Integer> getProductViewCounts() {
        return productViewCounter.getViewCounts();
    }

    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable Long id) {
        ProductResponse product = productService.getProductById(id);
        productViewCounter.recordView(id);
        return product;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> addProduct(
            @Valid @RequestBody ProductRequest request) {

        ProductResponse product = productService.addProduct(request);

        productEventProducer.publish(new ProductEvent(
                "PRODUCT_CREATED",
                product.id(),
                product.name(),
                product.price(),
                product.quantity(),
                Instant.now()
        ));

        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PutMapping("/{id}")
    public ProductResponse updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {

        ProductResponse product = productService.updateProduct(id, request);

        productEventProducer.publish(new ProductEvent(
                "PRODUCT_UPDATED",
                product.id(),
                product.name(),
                product.price(),
                product.quantity(),
                Instant.now()
        ));

        return product;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        ProductResponse product = productService.getProductById(id);

        productService.deleteProduct(id);

        productEventProducer.publish(new ProductEvent(
                "PRODUCT_DELETED",
                product.id(),
                product.name(),
                product.price(),
                product.quantity(),
                Instant.now()
        ));

        return ResponseEntity.noContent().build();
    }
}