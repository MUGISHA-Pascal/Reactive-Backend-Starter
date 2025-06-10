package com.starter.backend.services;

import com.starter.backend.dtos.UpdateInventoryDto;
import com.starter.backend.exceptions.ApiRequestException;
import com.starter.backend.models.Inventory;
import com.starter.backend.models.Product;
import com.starter.backend.repository.InventoryRepository;
import com.starter.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class InventoryService {
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private ProductRepository productRepository;

    public Mono<ResponseEntity<Product>> updateInventory(UUID id, UpdateInventoryDto updateInventoryDto) {
        if (updateInventoryDto == null) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        return productRepository.findById(id)
            .switchIfEmpty(Mono.error(new ApiRequestException("Product with id " + id + " not found")))
            .flatMap(product -> 
                inventoryRepository.findById(product.getInventoryId())
                    .switchIfEmpty(Mono.error(new ApiRequestException("Inventory not found for product")))
                    .flatMap(inventory -> {
                        inventory.setQuantity(updateInventoryDto.getQuantity());
                        inventory.setLocation(updateInventoryDto.getLocation());
                        return inventoryRepository.save(inventory);
                    })
                    .flatMap(savedInventory -> {
                        product.updateInventory(savedInventory);
                        return productRepository.save(product);
                    })
            )
            .map(ResponseEntity::ok)
            .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }
}
