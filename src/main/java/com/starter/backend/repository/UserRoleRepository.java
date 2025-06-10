package com.starter.backend.repository;

import com.starter.backend.models.UserRole;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface UserRoleRepository extends R2dbcRepository<UserRole, UUID> {
    Flux<UserRole> findByUserId(UUID userId);
    Flux<UserRole> findByRoleId(UUID roleId);
} 