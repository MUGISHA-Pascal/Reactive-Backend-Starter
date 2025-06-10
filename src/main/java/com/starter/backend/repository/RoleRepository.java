package com.starter.backend.repository;

import com.starter.backend.models.Role;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface RoleRepository extends R2dbcRepository<Role, UUID> {
    Mono<Role> findByName(String name);
}
