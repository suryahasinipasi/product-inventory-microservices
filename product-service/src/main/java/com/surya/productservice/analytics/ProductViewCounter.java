package com.surya.productservice.analytics;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ProductViewCounter {

    private final Map<Long, AtomicInteger> viewCounts =
            new ConcurrentHashMap<>();

    public void recordView(Long productId) {
        viewCounts
                .computeIfAbsent(productId, id -> new AtomicInteger())
                .incrementAndGet();
    }

    public Map<Long, Integer> getViewCounts() {
        Map<Long, Integer> snapshot = new HashMap<>();

        viewCounts.forEach(
                (id, count) -> snapshot.put(id, count.get())
        );

        return snapshot;
    }
}