package com.starter.backend.repository;

import com.starter.backend.models.Product;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import java.util.UUID;

@Repository
public interface ProductRepository extends R2dbcRepository<Product, UUID> {
    Flux<Product> findByCategory(String category);
    Flux<Product> findByInventoryId(UUID inventoryId);
}