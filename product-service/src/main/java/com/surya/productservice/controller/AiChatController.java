package com.surya.productservice.controller;

import com.surya.productservice.dto.AiChatRequest;
import com.surya.productservice.dto.AiChatResponse;
import com.surya.productservice.service.OllamaService;
import com.surya.productservice.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AiChatController {

    private final OllamaService ollamaService;
    private final ProductService productService;

    public AiChatController(
            OllamaService ollamaService,
            ProductService productService) {

        this.ollamaService = ollamaService;
        this.productService = productService;
    }

    @PostMapping("/chat")
    public ResponseEntity<AiChatResponse> chat(
            @RequestBody AiChatRequest request) {

        if (request.message() == null || request.message().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new AiChatResponse("Message is required."));
        }

        String answer = ollamaService.chat(
                request.message().trim(),
                productService.getAllProducts()
        );

        return ResponseEntity.ok(new AiChatResponse(answer));
    }
}