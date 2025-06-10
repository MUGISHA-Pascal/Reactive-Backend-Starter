package com.starter.backend.repository;

import com.starter.backend.models.Inventory;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface InventoryRepository extends R2dbcRepository<Inventory, UUID> {
    Flux<Inventory> findByLocation(String location);
}
