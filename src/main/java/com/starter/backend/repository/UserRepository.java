package com.starter.backend.repository;

import com.starter.backend.models.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UserRepository extends R2dbcRepository<User, UUID> {
    Mono<User> findByEmail(String email);
    Mono<User> findByMobile(String mobile);
    Mono<Boolean> existsByEmail(String email);
    Mono<Boolean> existsByMobile(String mobile);
}
