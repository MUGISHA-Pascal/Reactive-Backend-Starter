package com.starter.backend.repository;

import com.starter.backend.models.Inventory;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface InventoryRepository extends R2dbcRepository<Inventory, UUID> {
}
