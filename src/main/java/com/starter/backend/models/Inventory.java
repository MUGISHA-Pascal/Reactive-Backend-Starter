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
@AllArgsConstructor
@NoArgsConstructor
@Table("inventory")
public class Inventory {
    @Id
    private UUID id;
    private int quantity;
    private String location;

    public Inventory(int quantity, String location) {
        this.quantity = quantity;
        this.location = location;
    }
}
