package com.starter.backend.services;

import com.starter.backend.dtos.ProductDto;
import com.starter.backend.exceptions.ApiRequestException;
import com.starter.backend.models.Inventory;
import com.starter.backend.models.Product;
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

    @Autowired
    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    public Flux<Product> getAllProducts(){
        return productRepository.findAll();
    }

    public Mono<Product> addProduct(ProductDto productDto){
        Product product = new Product(
                productDto.getName(),
                productDto.getDescription(),
                productDto.getPrice(),
                productDto.getQuantity(),
                productDto.getCategory(),
                productDto.getInventory().getLocation(),
                productDto.getInventory().getQuantity()
        );
        return productRepository.save(product);
    }

    public Mono<Product> getProduct(UUID id){
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new ApiRequestException("Product with id " + id + " not found")));
    }

    public Mono<Product> updateProduct(UUID id, ProductDto productDto){
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new ApiRequestException("Product with id " + id + " not found")))
                .flatMap(existingProduct -> {
                    existingProduct.setName(productDto.getName());
                    existingProduct.setDescription(productDto.getDescription());
                    existingProduct.setPrice(productDto.getPrice());
                    existingProduct.setCategory(productDto.getCategory());
                    
                    Inventory inventory = new Inventory(productDto.getInventory().getQuantity(), productDto.getInventory().getLocation());
                    existingProduct.updateInventory(inventory);
                    
                    return productRepository.save(existingProduct);
                });
    }

    public Mono<Void> deleteProduct(UUID id){
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new ApiRequestException("Product with id " + id + " not found")))
                .flatMap(productRepository::delete);
    }

    public Flux<Product> findProductsByCategory(String category){
        return productRepository.findByCategory(category);
    }

    public Mono<Page<Product>> productsPagination(int page, int size, String column){
        Constants.validatePageNumberAndPageSize(page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.ASC, column);
        return productRepository.findAll()
                .collectList()
                .map(products -> new PageImpl<>(products, pageable, products.size()));
    }
}
