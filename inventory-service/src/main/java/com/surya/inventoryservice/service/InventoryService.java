package com.surya.inventoryservice.service;

import com.surya.inventoryservice.event.ProductEvent;
import com.surya.inventoryservice.model.Inventory;
import com.surya.inventoryservice.repository.InventoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public void processProductEvent(ProductEvent event) {
        switch (event.eventType()) {
            case "PRODUCT_CREATED", "PRODUCT_UPDATED" ->
                    createOrUpdateInventory(event);

            case "PRODUCT_DELETED" ->
                    inventoryRepository.deleteByProductId(event.productId());

            default -> throw new IllegalArgumentException(
                    "Unsupported event type: " + event.eventType()
            );
        }
    }

    private void createOrUpdateInventory(ProductEvent event) {
        Inventory inventory = inventoryRepository
                .findByProductId(event.productId())
                .orElseGet(() -> new Inventory(
                        event.productId(),
                        event.name(),
                        event.price(),
                        event.quantity(),
                        event.occurredAt()
                ));

        inventory.update(
                event.name(),
                event.price(),
                event.quantity(),
                event.occurredAt()
        );

        inventoryRepository.save(inventory);
    }

    @Transactional(readOnly = true)
    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Inventory getInventoryByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Inventory not found for product ID: " + productId
                ));
    }
}