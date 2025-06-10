package com.starter.backend.repository;

import com.starter.backend.enums.ERoleType;
import com.starter.backend.models.Role;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RoleRepository extends ReactiveCrudRepository<Role, UUID> {
    @Query("SELECT * FROM role WHERE name = :name")
    Mono<Role> findByName(ERoleType name);
}
