package com.surya.orderservice.repository;

import com.surya.orderservice.model.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository
        extends JpaRepository<CustomerOrder, Long> {
}