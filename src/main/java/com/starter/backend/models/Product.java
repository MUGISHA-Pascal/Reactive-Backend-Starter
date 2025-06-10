package com.starter.backend.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("product")
public class Product {
    @Id
    private UUID id;
    private String name;
    private String description;
    private int price;
    private int quantity;
    private UUID inventoryId;
    private String category;

    public Product(String name, String description, int price, int quantity, String category, String inventoryLocation, int inventoryQuantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.inventoryId = UUID.randomUUID(); // Generate a new UUID for inventory
    }

    public void updateInventory(Inventory inventory) {
        this.inventoryId = inventory.getId();
        this.quantity = inventory.getQuantity();
    }
}
