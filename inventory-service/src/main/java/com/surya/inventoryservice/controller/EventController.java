package com.surya.inventoryservice.controller;

import com.surya.inventoryservice.event.ProductEvent;
import com.surya.inventoryservice.service.EventLogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventLogService eventLogService;

    public EventController(EventLogService eventLogService) {
        this.eventLogService = eventLogService;
    }

    @GetMapping
    public List<ProductEvent> getRecentEvents() {
        return eventLogService.getRecentEvents();
    }
}