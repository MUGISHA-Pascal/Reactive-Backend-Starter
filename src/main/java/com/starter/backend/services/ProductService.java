package com.starter.backend.services;

import com.starter.backend.dtos.ProductDto;
import com.starter.backend.exceptions.ApiRequestException;
import com.starter.backend.models.Inventory;
import com.starter.backend.models.Product;
import com.starter.backend.repository.InventoryRepository;
import com.starter.backend.repository.ProductRepository;
import com.starter.backend.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    public Flux<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Mono<Product> addProduct(ProductDto productDto) {
        // Validate input
        if (productDto == null) {
            return Mono.error(new ApiRequestException("Product data cannot be null"));
        }
        if (productDto.getName() == null || productDto.getName().trim().isEmpty()) {
            return Mono.error(new ApiRequestException("Product name cannot be empty"));
        }
        if (productDto.getPrice() <= 0) {
            return Mono.error(new ApiRequestException("Product price must be greater than 0"));
        }
        if (productDto.getQuantity() < 0) {
            return Mono.error(new ApiRequestException("Product quantity cannot be negative"));
        }
        if (productDto.getCategory() == null || productDto.getCategory().trim().isEmpty()) {
            return Mono.error(new ApiRequestException("Product category cannot be empty"));
        }
        if (productDto.getInventory() == null) {
            return Mono.error(new ApiRequestException("Inventory information is required"));
        }
        if (productDto.getInventory().getQuantity() < 0) {
            return Mono.error(new ApiRequestException("Inventory quantity cannot be negative"));
        }
        if (productDto.getInventory().getLocation() == null || productDto.getInventory().getLocation().trim().isEmpty()) {
            return Mono.error(new ApiRequestException("Inventory location cannot be empty"));
        }

        // Create new inventory without manually setting ID
        Inventory inventory = new Inventory();
        inventory.setQuantity(productDto.getInventory().getQuantity());
        inventory.setLocation(productDto.getInventory().getLocation());

        // First save the inventory, then create and save the product
        return inventoryRepository.save(inventory)
            .flatMap(savedInventory -> {
                // Create product with basic information
                Product product = new Product(
                    productDto.getName(),
                    productDto.getDescription(),
                    productDto.getPrice(),
                    productDto.getQuantity(),
                    productDto.getCategory()
                );
                // Update product with inventory information
                product.updateInventory(savedInventory);
                return productRepository.save(product);
            })
            .onErrorResume(e -> {
                // If product creation fails, attempt to clean up the inventory
                if (e instanceof ApiRequestException) {
                    return Mono.error(e);
                }
                return Mono.error(new ApiRequestException("Failed to create product: " + e.getMessage()));
            });
    }

    public Mono<Product> getProduct(UUID id) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new ApiRequestException("Product with id " + id + " not found")));
    }

    public Mono<Product> updateProduct(UUID id, ProductDto productDto) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new ApiRequestException("Product with id " + id + " not found")))
                .flatMap(existingProduct -> 
                    inventoryRepository.findById(existingProduct.getInventoryId())
                        .switchIfEmpty(Mono.error(new ApiRequestException("Inventory not found for product")))
                        .flatMap(inventory -> {
                            inventory.setQuantity(productDto.getInventory().getQuantity());
                            inventory.setLocation(productDto.getInventory().getLocation());
                            return inventoryRepository.save(inventory);
                        })
                        .flatMap(savedInventory -> {
                            existingProduct.setName(productDto.getName());
                            existingProduct.setDescription(productDto.getDescription());
                            existingProduct.setPrice(productDto.getPrice());
                            existingProduct.setCategory(productDto.getCategory());
                            existingProduct.updateInventory(savedInventory);
                            return productRepository.save(existingProduct);
                        })
                );
    }

    public Mono<Void> deleteProduct(UUID id) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new ApiRequestException("Product with id " + id + " not found")))
                .flatMap(product -> 
                    inventoryRepository.deleteById(product.getInventoryId())
                        .then(productRepository.delete(product))
                );
    }

    public Flux<Product> findProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public Mono<Page<Product>> productsPagination(int page, int size, String column) {
        Constants.validatePageNumberAndPageSize(page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.ASC, column);
        return productRepository.findAll()
                .collectList()
                .map(products -> new PageImpl<>(products, pageable, products.size()));
    }
}
