package com.surya.inventoryservice.service;

import com.surya.inventoryservice.event.ProductEvent;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

@Service
public class EventLogService {

    private static final int MAX_EVENTS = 50;

    private final ConcurrentLinkedDeque<ProductEvent> events =
            new ConcurrentLinkedDeque<>();

    public void record(ProductEvent event) {
        events.addFirst(event);

        while (events.size() > MAX_EVENTS) {
            events.pollLast();
        }
    }

    public List<ProductEvent> getRecentEvents() {
        return events.stream().toList();
    }
}