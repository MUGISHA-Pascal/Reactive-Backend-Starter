package com.starter.backend.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Table("user_roles")
public class UserRole {
    @Id
    private UUID id;
    private UUID userId;
    private UUID roleId;

    public UserRole(UUID userId, UUID roleId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.roleId = roleId;
    }
} 