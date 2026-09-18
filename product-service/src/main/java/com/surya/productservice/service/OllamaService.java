package com.surya.productservice.service;

import com.surya.productservice.dto.ProductResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class OllamaService {

    private final RestClient ollamaClient;
    private final RestClient groqClient;
    private final String ollamaModel;
    private final String groqModel;
    private final boolean groqEnabled;

    public OllamaService(
            @Value("${ollama.base-url}") String ollamaBaseUrl,
            @Value("${ollama.model}") String ollamaModel,
            @Value("${GROQ_API_KEY:}") String groqApiKey,
            @Value("${groq.model:openai/gpt-oss-20b}") String groqModel) {

        this.ollamaClient = RestClient.builder()
                .baseUrl(ollamaBaseUrl)
                .build();

        this.ollamaModel = ollamaModel;
        this.groqModel = groqModel;
        this.groqEnabled =
                groqApiKey != null && !groqApiKey.isBlank();

        RestClient.Builder groqBuilder = RestClient.builder()
                .baseUrl("https://api.groq.com/openai/v1");

        if (groqEnabled) {
            groqBuilder.defaultHeader(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer " + groqApiKey.trim());
        }

        this.groqClient = groqBuilder.build();
    }

    public String chat(
            String userMessage,
            List<ProductResponse> products) {

        String exactPriceAnswer =
                answerPriceQuestion(userMessage, products);

        if (exactPriceAnswer != null) {
            return exactPriceAnswer;
        }

        String catalog = products.stream()
                .map(product ->
                        "- ID: " + product.id()
                                + ", Name: " + product.name()
                                + ", Price: $" + product.price()
                                + ", Quantity: " + product.quantity())
                .collect(Collectors.joining("\n"));

        if (catalog.isBlank()) {
            catalog = "No products are currently available.";
        }

        String systemPrompt =
                "You are the Surya Store product assistant.\n"
                        + "Answer using only the product catalog below.\n"
                        + "Never invent product names, prices, quantities, "
                        + "or categories.\n"
                        + "If information is unavailable, clearly say that "
                        + "it is not in the current Surya Store catalog.\n\n"
                        + "CURRENT PRODUCT CATALOG:\n"
                        + catalog;

        List<ChatMessage> messages = List.of(
                new ChatMessage("system", systemPrompt),
                new ChatMessage("user", userMessage)
        );

        return groqEnabled
                ? chatWithGroq(messages)
                : chatWithOllama(messages);
    }

    private String answerPriceQuestion(
            String userMessage,
            List<ProductResponse> products) {

        String question = userMessage.trim().replaceAll("\\s+", " ");

        var matcher = Pattern.compile(
                "(?i)(less than or equal to|at most|up to|no more than|"
                        + "less than|lower than|under|below|cheaper than|"
                        + "at least|more than|greater than|over|above)"
                        + "\\s*(?:\\$\\s*|USD\\s*)?"
                        + "([0-9]+(?:,[0-9]{3})*(?:\\.[0-9]{1,2})?)"
                        + "(?![0-9.,])")
                .matcher(question);

        if (matcher.find()) {
            String operator =
                    matcher.group(1).toLowerCase(Locale.ROOT);

            BigDecimal limit = new BigDecimal(
                    matcher.group(2).replace(",", ""));

            if (matcher.find() || question.matches(
                    "(?is).*\\b(and|or|between|except|excluding)\\b.*")) {

                return "Please ask for one price limit at a time, "
                        + "for example: Which products cost less than $200?";
            }

            String matches = products.stream()
                    .filter(product -> product.quantity() > 0)
                    .filter(product -> {
                        int comparison =
                                product.price().compareTo(limit);

                        return switch (operator) {
                            case "at most", "up to", "no more than",
                                 "less than or equal to" ->
                                    comparison <= 0;
                            case "at least" ->
                                    comparison >= 0;
                            case "more than", "greater than", "over",
                                 "above" ->
                                    comparison > 0;
                            default ->
                                    comparison < 0;
                        };
                    })
                    .map(product ->
                            "- " + product.name()
                                    + " — $"
                                    + product.price().toPlainString()
                                    + ", Quantity: "
                                    + product.quantity())
                    .collect(Collectors.joining("\n"));

            return matches.isBlank()
                    ? "No in-stock products match that price limit."
                    : "Catalog price filter (" + operator + " $"
                    + limit + "):\n" + matches;
        }

        if (question.matches(
                "(?is).*(\\$|\\b(price|prices|cost|costs|cheap|cheaper|"
                        + "cheapest|expensive|budget|under|below|above|"
                        + "less|more|dollars|usd)\\b).*")) {

            return "Please use a single price limit, such as "
                    + "'products under $200' or "
                    + "'products at most $200'.";
        }

        return null;
    }

    private String chatWithGroq(List<ChatMessage> messages) {
        GroqResponse response = groqClient.post()
                .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new GroqRequest(groqModel, messages, 300))
                .retrieve()
                .body(GroqResponse.class);

        if (response == null
                || response.choices() == null
                || response.choices().isEmpty()
                || response.choices().getFirst().message() == null
                || response.choices().getFirst().message().content() == null) {

            throw new IllegalStateException(
                    "Groq returned an empty response.");
        }

        return response.choices().getFirst().message().content();
    }

    private String chatWithOllama(List<ChatMessage> messages) {
        OllamaResponse response = ollamaClient.post()
                .uri("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new OllamaRequest(
                        ollamaModel,
                        messages,
                        false))
                .retrieve()
                .body(OllamaResponse.class);

        if (response == null
                || response.message() == null
                || response.message().content() == null) {

            throw new IllegalStateException(
                    "Ollama returned an empty response.");
        }

        return response.message().content();
    }

    private record ChatMessage(
            String role,
            String content) {
    }

    private record OllamaRequest(
            String model,
            List<ChatMessage> messages,
            boolean stream) {
    }

    private record OllamaResponse(
            ChatMessage message,
            boolean done) {
    }

    private record GroqRequest(
            String model,
            List<ChatMessage> messages,
            int max_completion_tokens) {
    }

    private record GroqChoice(
            ChatMessage message) {
    }

    private record GroqResponse(
            List<GroqChoice> choices) {
    }
}
