package com.surya.inventoryservice;

import com.surya.inventoryservice.event.ProductEvent;
import com.surya.inventoryservice.model.Inventory;
import com.surya.inventoryservice.repository.InventoryRepository;
import com.surya.inventoryservice.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        inventoryService = new InventoryService(inventoryRepository);
    }

    @Test
    void createsInventoryWhenProductCreatedEventArrives() {
        ProductEvent event = new ProductEvent(
                "PRODUCT_CREATED",
                10L,
                "Monitor",
                new BigDecimal("299.99"),
                5,
                Instant.now()
        );

        when(inventoryRepository.findByProductId(10L))
                .thenReturn(Optional.empty());

        inventoryService.processProductEvent(event);

        ArgumentCaptor<Inventory> captor =
                ArgumentCaptor.forClass(Inventory.class);

        verify(inventoryRepository).save(captor.capture());

        Inventory savedInventory = captor.getValue();

        assertEquals(10L, savedInventory.getProductId());
        assertEquals("Monitor", savedInventory.getProductName());
        assertEquals(new BigDecimal("299.99"), savedInventory.getPrice());
        assertEquals(5, savedInventory.getQuantity());
    }

    @Test
    void deletesInventoryWhenProductDeletedEventArrives() {
        ProductEvent event = new ProductEvent(
                "PRODUCT_DELETED",
                10L,
                "Monitor",
                new BigDecimal("299.99"),
                5,
                Instant.now()
        );

        inventoryService.processProductEvent(event);

        verify(inventoryRepository).deleteByProductId(10L);
    }
}